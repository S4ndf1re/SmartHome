package plugin.implementations.controller

import com.hivemq.client.mqtt.mqtt5.Mqtt5Client
import plugin.Plugin
import plugin.implementations.plugin.IPlugin

/**
 * IController is the interface, which controllers must implement, in order to be able to function as one.
 */
interface IController {

    /**
     * init gets called right after the program started
     * @param handler A handler to a already initialized Mqtt5Client
     * @return True if successful, false otherwise
     */
    fun init(handler: Mqtt5Client, pluginList: Map<String, Plugin<IPlugin>>): Boolean


    /**
     * close will get called at the end of the lifecycle of the PluginManager
     * This function can be safely used to clean up and unsubscribe to topics.
     * @param handler A handler to a already initialized Mqtt5Client
     */
    fun close(handler: Mqtt5Client)

}
