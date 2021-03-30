package plugin

import com.hivemq.client.mqtt.mqtt5.Mqtt5Client
import util.ExtensionLoader

typealias Path = String
typealias Topic = String

/**
 * IPlugin is the interface, which plugins must implement, in order to be able to function as one.
 */

interface IPlugin {

    fun init(handler: Mqtt5Client)
    fun getMqttSubscriptionsTopics(): ArrayList<Topic>
    fun getMqttPublishTopics(): ArrayList<Topic>
    fun getHttpHandles(): Map<Path, () -> String>
    fun close(handler: Mqtt5Client)

}

/**
 * PluginSystem defines the plugin system loader and usage.
 */
class PluginSystem {
    companion object Factory {
        fun create(f: PluginSystem.() -> Unit): PluginSystem {
            val system = PluginSystem()
            system.f()
            return system
        }

        /**
         * loadFromDir will take any directory and load all possible plugins from it.
         * In order for a plugin to be registered as such, it must not be in a package (top level)
         * and has to have a class named Plugin that inherits from IPlugin.
         * @param dir The directory to load plugins from.
         * @return The complete and loaded PluginSystem
         */
        fun loadFromDir(dir: String): PluginSystem {
            val system = PluginSystem()
            val loader = ExtensionLoader<IPlugin>()
            system.pluginList = loader.loadFromDir(dir, "Plugin", IPlugin::class.java)

            return system
        }
    }

    private var pluginList: MutableMap<String, IPlugin> = mutableMapOf()

    /**
     * set adds or sets a value in the plugin system
     * @param key The name of the plugin in order to identify it uniquely
     * @param plugin The plugin that will get loaded
     * @return True if successful
     */
    operator fun set(key: String, plugin: IPlugin): Boolean {
        this.pluginList[key] = plugin
        return this.pluginList[key] == plugin
    }

    /**
     * get allows to retrieve a plugin from the system.
     * @param key The name of the plugin
     * @return The Plugin, if found. Else null
     */
    operator fun get(key: String): IPlugin? {
        return this.pluginList[key]
    }

    /**
     * remove can remove a plugin fom the list, if the list contains it.
     * @param key The name of the plugin
     * @return The plugin that got removed. If no plugin was removed, null is returned
     */
    fun remove(key: String): IPlugin? {
        return this.pluginList.remove(key)
    }

    /**
     * start will initialize all plugins
     * @param client Defines the global MQTT client
     */
    fun start(client: Mqtt5Client) {
        for ((_, v) in this.pluginList) {
            v.init(client)
        }
    }

    /**
     * stop will close all plugins
     * @param client Defines the global MQTT client
     */
    fun stop(client: Mqtt5Client) {
        for ((_, v) in this.pluginList) {
            v.close(client)
        }
    }

}