package plugin.implementations.controller

import com.github.s4ndf1re.ILogger
import com.hivemq.client.mqtt.mqtt3.Mqtt3Client
import org.ktorm.database.Database
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
    fun init(
        handler: Mqtt3Client,
        database: Database,
        pluginList: Map<String, Plugin<IPlugin>>,
        logger: ILogger
    ): Boolean


    /**
     * close will get called at the end of the lifecycle of the PluginManager
     * This function can be safely used to clean up and unsubscribe to topics.
     * @param handler A handler to a already initialized Mqtt5Client
     */
    fun close()

}
