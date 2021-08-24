import com.hivemq.client.mqtt.mqtt3.Mqtt3Client
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish
import gui.Container
import org.ktorm.database.Database
import plugin.implementations.plugin.IPlugin

class Plugin : IPlugin {

    private var handler: Mqtt3Client? = null
    private var handlers: MutableMap<String, ESP8266Handler> = mutableMapOf()
    private var database: Database? = null

    override fun init(handler: Mqtt3Client, database: Database): Boolean {
        handler.toAsync().subscribeWith().topicFilter("doorlock/+/status").callback { pub -> this.statusHandler(pub) }
            .send().join()
        this.handler = handler
        this.database = database
        val esp = ESP8266Handler("01", handler, database)
        esp.authenticate()
        return true
    }

    override fun getContainers(): List<Container> {
        return handlers.map { it.value.getContainer() }
    }

    override fun close() {
        handlers.forEach {
            it.value.close()
        }
        handler?.toAsync()?.unsubscribeWith()?.topicFilter("doorlock/+/status")?.send()?.join()
    }

    private fun statusHandler(publish: Mqtt3Publish) {
        val id = publish.topic.levels[1]
        println("Found id: $id")
        if (!handlers.containsKey(id) && handler != null && database != null) {
            println("Inserted $id to list")
            handlers[id] = ESP8266Handler(id, handler!!, database!!)
        }
    }
   
}