import com.github.s4ndf1re.ILogger
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish
import gui.Container
import org.ktorm.database.Database
import org.ktorm.dsl.*
import java.util.*

class ESP8266Handler(
    private var id: String,
    private val mqtt: Mqtt3Client,
    private val database: Database,
    private val logger: ILogger
) {
    private var status: Status = Status.INACTIVE
    private var mode: Mode = Mode.IDLE
    private var currentUid: ByteArray = ByteArray(0)
    private var currentData: ByteArray = ByteArray(0)
    private var dataCount = 0
    private var user: String? = null

    init {
        mqtt.toAsync().subscribeWith().topicFilter("doorlock/${id}/status").qos(MqttQos.EXACTLY_ONCE)
            .callback { statusCallback(it) }.send()
        mqtt.toAsync().subscribeWith().topicFilter("doorlock/${id}/error").qos(MqttQos.EXACTLY_ONCE)
            .callback { errorCallback(it) }.send()
        mqtt.toAsync().subscribeWith().topicFilter("doorlock/${id}/write/ok").qos(MqttQos.EXACTLY_ONCE)
            .callback { writeOkCallback(it) }.send()
        mqtt.toAsync().subscribeWith().topicFilter("doorlock/${id}/read/uid").qos(MqttQos.EXACTLY_ONCE)
            .callback { uidCallback(it) }.send()
        mqtt.toAsync().subscribeWith().topicFilter("doorlock/${id}/read/data").qos(MqttQos.EXACTLY_ONCE)
            .callback { dataCallback(it) }.send()
    }

    fun close() {
        mqtt.toAsync().unsubscribeWith().topicFilter("doorlock/${id}/status").send()
        mqtt.toAsync().unsubscribeWith().topicFilter("doorlock/${id}/error").send()
        mqtt.toAsync().unsubscribeWith().topicFilter("doorlock/${id}/write/ok").send()
        mqtt.toAsync().unsubscribeWith().topicFilter("doorlock/${id}/read/uid").send()
        mqtt.toAsync().unsubscribeWith().topicFilter("doorlock/${id}/read/data").send()
    }

    private fun statusCallback(publish: Mqtt3Publish) {
        if (publish.payload.isPresent) {
            if (publish.payloadAsBytes.contentEquals("true".toByteArray())) {
                this.status = Status.ACTIVE
            } else {
                this.status = Status.INACTIVE
            }
            this.logger.info { "Changed $id to $status" }
        }
    }

    private fun errorCallback(publish: Mqtt3Publish) {
        if (publish.payload.isPresent) {
            logger.error { String(publish.payloadAsBytes) }
        }
    }

    private fun writeOkCallback(publish: Mqtt3Publish) {
        if (publish.payload.isPresent) {
            if (publish.payloadAsBytes.contentEquals("true".toByteArray())) {
                logger.info { "Data for ChipID $id was written successfully. Update DB" }
                database.update(Mifare1k) {
                    set(it.data, currentData)
                    where {
                        it.uid eq currentUid
                    }
                }

                if (this.mode == Mode.CHECKING) {
                    println("Opening door for ${this.id}")
                    this.performDoorOpen()
                }
            }
        }
    }

    private fun uidCallback(publish: Mqtt3Publish) {
        if (publish.payload.isPresent) {
            this.currentUid = Base64.getDecoder().decode(publish.payloadAsBytes)
            dataCount++
        }
        handleRead()
    }

    private fun dataCallback(publish: Mqtt3Publish) {
        if (publish.payload.isPresent) {
            this.currentData = Base64.getDecoder().decode(publish.payloadAsBytes)
            this.dataCount++
        }
        handleRead()
    }


    private fun handleRead() {
        if (this.dataCount % 2 == 0 && this.dataCount > 0 && this.status == Status.ACTIVE) {
            this.dataCount = 0
            when (this.mode) {
                Mode.IDLE -> return
                Mode.AUTHENTICATING -> this.authenticate()
                Mode.CHECKING -> this.check()
            }
        }
    }

    private fun doesCurrentChipExist(): Boolean {
        var count = 0
        database.from(Mifare1k).select().where {
            Mifare1k.uid eq this.currentUid
        }.limit(1).forEach { count++ }
        return count > 0
    }

    private fun insertCurrentChip() {
        database.insert(Mifare1k) {
            set(it.uid, currentUid)
            set(it.data, "".toByteArray())
            set(it.username, user)
        }
    }

    private fun randomByteArray(): ByteArray {
        val random = Random()
        val array = ByteArray(MAX_BYTES)
        random.nextBytes(array)
        return array
    }

    private fun authenticate() {
        if (this.user == null) {
            return
        }

        // Determine if chip already exists. If no, insert chip into db
        if (!this.doesCurrentChipExist()) {
            this.insertCurrentChip()
        }

        this.currentData = this.randomByteArray()
        this.performWrite()
    }

    private fun performWrite() {
        val payload = Base64.getEncoder().encode(this.currentData)
        mqtt.toAsync().publishWith().topic("doorlock/${this.id}/write/data").payload(payload).send().join()
    }

    private fun ByteArray.toHexString(): String {
        return this.joinToString("") {
            java.lang.String.format("%02x", it)
        }
    }

    private fun check() {
        val validUidContent = database.from(Mifare1k).select().where {
            Mifare1k.uid eq this.currentUid
        }.map {
            it[Mifare1k.data].contentEquals(this.currentData)
        }
        if (validUidContent.any { it }) {
            this.authenticate()
        } else {
            logger.error { "No existing uid found in DB or Content is different" }
        }
    }

    private fun performDoorOpen() {
        mqtt.toAsync().publishWith().topic("doorlock/${this.id}/open").payload("true".toByteArray()).send().join()
    }

    fun getContainer(): Container {
        return Container.create("Doorlock-${this.id}") {
            button("check-${id}") {
                text = "Check"
                onClick = {
                    user = it
                    mode = Mode.CHECKING
                    logger.info { "Changed mode to CHECKING" }
                }
            }
            button("auth-${id}") {
                text = "Authenticate"
                onClick = {
                    user = it
                    mode = Mode.AUTHENTICATING
                    logger.info { "Changed mode to AUTHENTICATING" }
                }
            }
            button("stop-${id}") {
                text = "Stop"
                onClick = {
                    user = it
                    mode = Mode.IDLE
                    logger.info { "Changed mode to IDLE" }
                }
            }
        }
    }
}