import com.hivemq.client.mqtt.mqtt3.Mqtt3Client
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish
import org.ktorm.database.Database
import org.ktorm.dsl.*
import java.util.*

enum class Status {
    ACTIVE,
    INACTIVE
}

enum class Mode {
    CHECKING,
    AUTHENTICATING,
    IDLE
}

class ESP8266Handler(var id: String, val mqtt: Mqtt3Client, val database: Database) {
    var status: Status = Status.INACTIVE
    var mode: Mode = Mode.IDLE
    var currentUid: ByteArray = ByteArray(0)
    var currentData: ByteArray = ByteArray(0)
    var dataCount = 0
    var user: String? = null

    init {
        mqtt.toAsync().subscribeWith().topicFilter("doorlock/${id}/status").callback { statusCallback(it) }.send()
            .join()
        mqtt.toAsync().subscribeWith().topicFilter("doorlock/${id}/error").callback { errorCallback(it) }.send().join()
        mqtt.toAsync().subscribeWith().topicFilter("doorlock/${id}/write/ok").callback { writeOkCallback(it) }.send()
            .join()
        mqtt.toAsync().subscribeWith().topicFilter("doorlock/${id}/read/uid").callback { uidCallback(it) }.send().join()
        mqtt.toAsync().subscribeWith().topicFilter("doorlock/${id}/read/data").callback { dataCallback(it) }.send()
            .join()
    }

    fun close() {
        mqtt.toAsync().unsubscribeWith().topicFilter("doorlock/${id}/status").send().join()
        mqtt.toAsync().unsubscribeWith().topicFilter("doorlock/${id}/error").send().join()
        mqtt.toAsync().unsubscribeWith().topicFilter("doorlock/${id}/write/ok").send().join()
        mqtt.toAsync().unsubscribeWith().topicFilter("doorlock/${id}/read/uid").send().join()
        mqtt.toAsync().unsubscribeWith().topicFilter("doorlock/${id}/read/data").send().join()
    }

    private fun statusCallback(publish: Mqtt3Publish) {
        if (publish.payload.isPresent) {
            if (publish.payloadAsBytes.contentEquals("true".toByteArray())) {
                this.status = Status.ACTIVE
            } else {
                this.status = Status.INACTIVE
            }
        }
    }

    private fun errorCallback(publish: Mqtt3Publish) {
        if (publish.payload.isPresent) {
            println(publish.payloadAsBytes.toString())
        }
    }

    private fun writeOkCallback(publish: Mqtt3Publish) {
        if (publish.payload.isPresent) {
            if (publish.payloadAsBytes.contentEquals("true".toByteArray())) {
                TODO("Implement Database write and door open")
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

    fun authenticate() {
        if (this.user == null) {
            return
        }

        // Determine if chip already exists. If no, insert chip into db
        if (!this.doesCurrentChipExist()) {
            this.insertCurrentChip()
        }
    }

    private fun check() {
        
    }
}