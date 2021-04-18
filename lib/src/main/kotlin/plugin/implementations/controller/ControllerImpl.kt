package plugin.implementations.controller

import com.hivemq.client.mqtt.mqtt5.Mqtt5Client
import plugin.Plugin
import plugin.PluginDescriptor
import plugin.implementations.plugin.IPlugin

class ControllerImpl(
    override val name: String,
    override val descriptor: PluginDescriptor,
    override val pluginClassMap: Map<String, IController>,
    private val plugins: Map<String, Plugin<IPlugin>>,
) : Plugin<IController>() {

    override fun start(client: Mqtt5Client) {
        for ((name, v) in this.pluginClassMap) {
            println("Starting $name")
            v.init(client, plugins)
            println("Done")
        }
    }

    override fun stop(client: Mqtt5Client) {
        for ((name, v) in this.pluginClassMap) {
            println("Stopping $name")
            v.close(client)
            println("Done")
        }
    }

}