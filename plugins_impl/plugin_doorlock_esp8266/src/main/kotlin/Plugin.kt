import com.github.s4ndf1re.ILogger
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client
import com.hivemq.client.mqtt.mqtt3.message.publish.Mqtt3Publish
import gui.Container
import org.ktorm.database.Database
import plugin.implementations.plugin.IPlugin
import java.util.*

class Plugin : IPlugin {

    private var handler: Optional<Mqtt3Client> = Optional.empty()
    private var handlers: MutableMap<String, ESP8266Handler> = mutableMapOf()
    private var database: Optional<Database> = Optional.empty()
    private var logger: Optional<ILogger> = Optional.empty()

    override fun init(handler: Mqtt3Client, database: Database, logger: ILogger): Boolean {
        handler.toAsync().subscribeWith().topicFilter("doorlock/+/status").callback { pub -> this.statusHandler(pub) }
            .send().join()
        this.handler = Optional.of(handler)
        this.database = Optional.of(database)
        this.logger = Optional.of(logger)
        return true
    }

    override fun getContainers(): List<Container> {
        return handlers.map { it.value.getContainer() }
    }

    override fun close() {
        handlers.forEach {
            it.value.close()
        }
        handler.ifPresent { hndl ->
            hndl.toAsync().unsubscribeWith().topicFilter("doorlock/+/status").send().join()
        }
    }

    private fun statusHandler(publish: Mqtt3Publish) {
        val id = publish.topic.levels[1]
        handler.ifPresent { hndl ->
            database.ifPresent { db ->
                logger.ifPresent { log ->
                    if (!handlers.containsKey(id)) {
                        log.info { "Found id: $id" }
                        log.info { "Inserted $id to list" }
                        handlers[id] = ESP8266Handler(id, hndl, db, log.createNode("Doorhandler-$id"))
                    }
                }
            }
        }
    }

}