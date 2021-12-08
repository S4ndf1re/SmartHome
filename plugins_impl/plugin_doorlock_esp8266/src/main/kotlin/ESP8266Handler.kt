import com.github.s4ndf1re.ILogger
import com.hivemq.client.mqtt.datatypes.MqttQos
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish
import gui.Alert
import gui.Container
import gui.Data
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
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

    private var user: String? = null
    private var data: Data? = null

    init {
        mqtt.toAsync().subscribeWith().topicFilter("doorlock/${id}/status").qos(MqttQos.EXACTLY_ONCE)
            .callback { statusCallback(it) }.send()
        mqtt.toAsync().subscribeWith().topicFilter("doorlock/${id}/error").qos(MqttQos.EXACTLY_ONCE)
            .callback { errorCallback(it) }.send()
        mqtt.toAsync().subscribeWith().topicFilter("doorlock/${id}/write/ok").qos(MqttQos.EXACTLY_ONCE)
            .callback { writeOkCallback(it) }.send()
        mqtt.toAsync().subscribeWith().topicFilter("doorlock/${id}/read").qos(MqttQos.EXACTLY_ONCE)
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
            println("Changed $id to $status")
        }
    }

    private fun errorCallback(publish: Mqtt3Publish) {
        if (publish.payload.isPresent) {
            println(String(publish.payloadAsBytes))
        }
    }

    private fun writeOkCallback(publish: Mqtt3Publish) {
        if (publish.payload.isPresent) {
            if (publish.payloadAsBytes.contentEquals("true".toByteArray())) {
                println("Data for ChipID $id was written successfully. Update DB")
                database.update(Mifare1k) {
                    set(it.data, currentData)
                    where {
                        it.uid eq currentUid
                    }
                }

                //if (this.mode == Mode.CHECKING) {
                //    println("Opening door for ${this.id}")
                //    this.performDoorOpen()
                //}
            }
        }
    }

    @Synchronized
    private fun dataCallback(publish: Mqtt3Publish) {
        if (publish.payload.isPresent) {
            println("Received payload: ${publish.payload}")
            val tempData = publish.payloadAsBytes
            val json = Json
            val tempPayload = json.decodeFromString<Payload>(String(tempData))
            this.currentData = Base64.getDecoder().decode(tempPayload.data.toByteArray())
            this.currentUid = Base64.getDecoder().decode(tempPayload.uid.toByteArray())
            handleRead()
        }
    }


    private fun handleRead() {
        println("Handle read with Mode: $mode")
        when (this.mode) {
            Mode.IDLE -> return
            Mode.AUTHENTICATING -> this.authenticate()
            Mode.CHECKING -> this.check()
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
            user?.let { usr -> set(it.username, usr) }
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

    private fun check() {
        println("Trying to fetch from db")
        val validUidContent = database.from(Mifare1k).select().where {
            Mifare1k.uid eq this.currentUid
        }.map {
            true
            //row[Mifare1k.data].contentEquals(this.currentData)
        }
        if (validUidContent.any { it }) {
            println("Found valid db entry. Authetication now")
            this.authenticate()
            println("Opening door for ${this.id}")
            this.performDoorOpen()
        } else {
            println("No existing uid found in DB or Content is different")
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
                    data?.update(it, Alert("alert-$id", "Changed mode to ${Mode.CHECKING}"))
                }
            }
            button("auth-${id}") {
                text = "Authenticate"
                onClick = {
                    user = it
                    mode = Mode.AUTHENTICATING
                    data?.update(it, Alert("alert-$id", "Changed mode to ${Mode.AUTHENTICATING}"))
                }
            }
            button("stop-${id}") {
                text = "Stop"
                onClick = {
                    user = it
                    mode = Mode.IDLE
                    data?.update(it, Alert("alert-$id", "Changed mode to ${Mode.IDLE}"))
                }
            }
            data = data("data-${id}") {}

            onInit = { user ->
                data?.update(user, Alert("alert-$id", ""))
            }
        }
    }
}