package plugin.implementations.plugin

import com.hivemq.client.mqtt.mqtt3.Mqtt3Client
import org.ktorm.database.Database
import plugin.Plugin
import plugin.PluginDescriptor

class PluginImpl(
    override val name: String,
    override val descriptor: PluginDescriptor,
    override val pluginClassMap: Map<String, IPlugin>,
) : Plugin<IPlugin>() {


    override fun start(client: Mqtt3Client, database: Database) {
        for ((name, v) in this.pluginClassMap) {
            println("Starting $name")
            v.init(client, database)
            println("Done")
        }
    }

    override fun stop() {
        for ((name, v) in this.pluginClassMap) {
            println("Stopping $name")
            v.close()
            println("Done")
        }
    }

}