package plugin

import com.hivemq.client.mqtt.mqtt3.Mqtt3Client
import java.io.File

/**
 * PluginSystem defines the plugin system loader and usage.
 */
class PluginSystem<T> {
    companion object Factory {

        /**
         * loadFromDir will take any directory and load all possible plugins from it.
         * In order for a plugin to be registered as such, it must not be in a package (top level)
         * and has to have a class named Plugin that inherits from IPlugin.
         * @param path The directory to load plugins from.
         * @return The complete and loaded PluginSystem
         */
        inline fun <reified T> loadFromDir(path: Path, creator: PluginCreator<T>): PluginSystem<T> {
            val system = PluginSystem<T>()
            val pluginDir = File(path)
            val dirs = pluginDir.listFiles() ?: return system
            for (dir in dirs) {

                if (!dir.isDirectory) {
                    return system
                }
                val descriptor = PluginDescriptor.load(dir.path + File.separator + "plugin.xml") ?: continue
                val loader = ExtensionLoader<T>()
                val classMap = loader.loadFromDir(
                    dir = dir.path + File.separator + descriptor.jarName,
                    classnames = descriptor.pluginClass,
                    parent = T::class.java
                )

                system.pluginList[dir.name] = creator.create(
                    descriptor = descriptor,
                    name = dir.name,
                    pluginClassMap = classMap
                )
            }
            return system
        }
    }

    var pluginList: MutableMap<String, Plugin<T>> = mutableMapOf()

    /**
     * set adds or sets a value in the plugin system
     * @param key The name of the plugin in order to identify it uniquely
     * @param plugin The plugin that will get loaded
     * @return True if successful
     */
    operator fun set(key: String, plugin: Plugin<T>): Boolean {
        this.pluginList[key] = plugin
        return this.pluginList[key] == plugin
    }

    /**
     * get allows to retrieve a plugin from the system.
     * @param key The name of the plugin
     * @return The Plugin, if found. Else null
     */
    operator fun get(key: String): Plugin<T>? {
        return this.pluginList[key]
    }

    /**
     * remove can remove a plugin fom the list, if the list contains it.
     * @param key The name of the plugin
     * @return The plugin that got removed. If no plugin was removed, null is returned
     */
    fun remove(key: String): Plugin<T>? {
        return this.pluginList.remove(key)
    }

    /**
     * start will initialize all plugins
     * @param client Defines the global MQTT client
     */
    fun start(client: Mqtt3Client) {
        for ((_, v) in this.pluginList) {
            v.start(client)
        }
    }

    /**
     * stop will close all plugins
     * @param client Defines the global MQTT client
     */
    fun stop(client: Mqtt3Client) {
        for ((_, v) in this.pluginList) {
            v.stop(client)
        }
    }

}
