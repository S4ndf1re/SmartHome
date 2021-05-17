package plugin.implementations.controller

import com.hivemq.client.mqtt.mqtt3.Mqtt3Client
import org.ktorm.database.Database
import plugin.Plugin
import plugin.PluginDescriptor
import plugin.implementations.plugin.IPlugin

class ControllerImpl(
    override val name: String,
    override val descriptor: PluginDescriptor,
    override val pluginClassMap: Map<String, IController>,
    private val plugins: Map<String, Plugin<IPlugin>>,
) : Plugin<IController>() {

    override fun start(client: Mqtt3Client, database: Database) {
        for ((name, v) in this.pluginClassMap) {
            println("Starting $name")
            v.init(client, database, plugins)
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