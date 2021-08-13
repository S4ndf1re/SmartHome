import com.hivemq.client.mqtt.mqtt3.Mqtt3Client
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish
import gui.Container
import org.ktorm.database.Database
import plugin.implementations.plugin.IPlugin
import java.util.logging.Handler

class Plugin : IPlugin {

    private var handler: Mqtt3Client? = null
    private var handlers: MutableMap<String, Handler> = mutableMapOf()

    override fun init(handler: Mqtt3Client, database: Database): Boolean {
        handler.toAsync().subscribeWith().topicFilter("doorlock/+/status").callback { pub -> this.status_handler(pub) }
            .send().join()
        this.handler = handler
        val esp = ESP8266Handler("01", handler, database)
        esp.authenticate()
        return true
    }

    override fun getContainers(): List<Container> {
        return listOf()
    }

    override fun close() {
        handler?.toAsync()?.unsubscribeWith()?.topicFilter("doorlock/+/status")?.send()?.join()
    }

    private fun status_handler(publish: Mqtt3Publish) {
        println(publish.payloadAsBytes)
    }

}