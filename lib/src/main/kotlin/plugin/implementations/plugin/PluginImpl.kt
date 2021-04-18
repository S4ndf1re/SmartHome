package plugin.implementations.plugin

import com.hivemq.client.mqtt.mqtt5.Mqtt5Client
import plugin.Plugin
import plugin.PluginDescriptor

class PluginImpl(
    override val name: String,
    override val descriptor: PluginDescriptor,
    override val pluginClassMap: Map<String, IPlugin>,
) : Plugin<IPlugin>() {


    override fun start(client: Mqtt5Client) {
        for ((name, v) in this.pluginClassMap) {
            println("Starting $name")
            v.init(client)
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