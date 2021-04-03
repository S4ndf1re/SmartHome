package plugin

import com.hivemq.client.mqtt.mqtt5.Mqtt5Client
import mu.KotlinLogging
import java.io.File

/**
 * PluginSystem defines the plugin system loader and usage.
 */
class PluginSystem {
    companion object Factory {

        /**
         * loadFromDir will take any directory and load all possible plugins from it.
         * In order for a plugin to be registered as such, it must not be in a package (top level)
         * and has to have a class named Plugin that inherits from IPlugin.
         * @param path The directory to load plugins from.
         * @return The complete and loaded PluginSystem
         */
        fun loadFromDir(path: Path): PluginSystem {
            val system = PluginSystem()
            val pluginDir = File(path)
            val dirs = pluginDir.listFiles() ?: return system
            for (dir in dirs) {
                val descriptor = PluginDescriptor.load(dir.path + "plugin.xml")
                val loader = ExtensionLoader<IPlugin>()
                val classMap = loader.loadFromDir(
                    dir = dir.path + descriptor.jarName,
                    classnames = descriptor.pluginClass,
                    parent = IPlugin::class.java
                )

                system.pluginList[dir.name] = Plugin(
                    descriptor = descriptor,
                    name = dir.name,
                    pluginClassMap = classMap
                )
            }
            return system
        }
    }

    private var pluginList: MutableMap<String, Plugin> = mutableMapOf()
    private val logger = KotlinLogging.logger {}

    /**
     * set adds or sets a value in the plugin system
     * @param key The name of the plugin in order to identify it uniquely
     * @param plugin The plugin that will get loaded
     * @return True if successful
     */
    operator fun set(key: String, plugin: Plugin): Boolean {
        this.pluginList[key] = plugin
        return this.pluginList[key] == plugin
    }

    /**
     * get allows to retrieve a plugin from the system.
     * @param key The name of the plugin
     * @return The Plugin, if found. Else null
     */
    operator fun get(key: String): Plugin? {
        return this.pluginList[key]
    }

    /**
     * remove can remove a plugin fom the list, if the list contains it.
     * @param key The name of the plugin
     * @return The plugin that got removed. If no plugin was removed, null is returned
     */
    fun remove(key: String): Plugin? {
        return this.pluginList.remove(key)
    }

    /**
     * start will initialize all plugins
     * @param client Defines the global MQTT client
     */
    fun start(client: Mqtt5Client) {
        logger.info { "Starting all Plugins" }
        for ((_, v) in this.pluginList) {
            logger.info { "\tStarting Plugin ${v.name}" }
            for ((name, iPlug) in v.pluginClassMap) {
                logger.info { "\t\tStarting $name" }
                iPlug.init(client)
                logger.info { "\t\tDone" }
            }
            logger.info { "\tDone" }
        }
        logger.info { "Done" }
    }

    /**
     * stop will close all plugins
     * @param client Defines the global MQTT client
     */
    fun stop(client: Mqtt5Client) {
        logger.info { "Starting graceful shutdown" }
        for ((_, v) in this.pluginList) {
            logger.info { "\tClosing Plugin ${v.name}" }
            for ((name, iPlug) in v.pluginClassMap) {
                logger.info { "\t\tClosing $name." }
                iPlug.close(client)
                logger.info { "\t\tDone" }
            }
            logger.info { "\tDone" }
        }
        logger.info { "Done" }
    }

}
