package plugin

import com.hivemq.client.mqtt.mqtt5.Mqtt5Client


abstract class Plugin<T> {

    abstract val name: String
    abstract val descriptor: PluginDescriptor
    abstract val pluginClassMap: Map<String, T>

    abstract fun start(client: Mqtt5Client)

    abstract fun stop(client: Mqtt5Client)

}
