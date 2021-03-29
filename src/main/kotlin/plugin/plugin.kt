package plugin

import com.hivemq.client.mqtt.mqtt5.Mqtt5Client

interface IPlugin {

    fun init(handler: Mqtt5Client)
    fun close()

}


class PluginSystem {
    companion object Factory {
        fun create(f: PluginSystem.() -> Unit): PluginSystem {
            val system = PluginSystem()
            system.f()
            return system
        }
    }

    private var pluginList: MutableMap<String, IPlugin> = mutableMapOf()

    operator fun set(key: String, plugin: IPlugin): Boolean {
        this.pluginList[key] = plugin
        return this.pluginList[key] == plugin
    }

    operator fun get(key: String): IPlugin? {
        return this.pluginList[key]
    }

    fun remove(key: String): IPlugin? {
        return this.pluginList.remove(key)
    }

    fun start(client: Mqtt5Client) {
        for ((k, v) in this.pluginList) {
            v.init(client)
        }
    }

    fun stop() {
        for ((k, v) in this.pluginList) {
            v.close()
        }
    }
}