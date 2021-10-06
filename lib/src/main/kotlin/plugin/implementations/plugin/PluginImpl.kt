package plugin.implementations.plugin

import com.github.s4ndf1re.ILogger
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client
import org.ktorm.database.Database
import plugin.Plugin
import plugin.PluginDescriptor

class PluginImpl(
    override val name: String,
    override val descriptor: PluginDescriptor,
    override val pluginClassMap: Map<String, IPlugin>,
) : Plugin<IPlugin>() {
    private var logger: ILogger? = null

    override fun start(client: Mqtt3Client, database: Database, logger: ILogger) {
        for ((name, v) in this.pluginClassMap) {
            logger.info { "Starting $name" }
            v.init(client, database, logger.createNode(name))
            logger.info { "Done" }
        }
        this.logger = logger
    }

    override fun stop() {
        for ((name, v) in this.pluginClassMap) {
            this.logger?.info { "Stopping $name" }
            v.close()
            this.logger?.info { "Done" }
        }
    }

}