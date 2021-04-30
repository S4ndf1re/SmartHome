package plugin

import com.hivemq.client.mqtt.mqtt3.Mqtt3Client


abstract class Plugin<T> {

    abstract val name: String
    abstract val descriptor: PluginDescriptor
    abstract val pluginClassMap: Map<String, T>

    abstract fun start(client: Mqtt3Client)

    abstract fun stop(client: Mqtt3Client)

}
