package plugin

import com.hivemq.client.mqtt.mqtt5.Mqtt5Client

/**
 * IPlugin is the interface, which plugins must implement, in order to be able to function as one.
 */
interface IPlugin {

    /**
     * init gets called right after the program started
     * @param handler A handler to a already initialized Mqtt5Client
     * @return True if successful, false otherwise
     */
    fun init(handler: Mqtt5Client): Boolean

    /**
     * getMqttSubscriptionsTopics will return a list of all subscribed topics
     * In case of non graceful exit, the plugin manager could unsubscribe all topics
     * @return A list of all Mqtt Subscription topics
     */
    fun getMqttSubscriptionsTopics(): ArrayList<Topic>

    /**
     * getMqttPublishTopics will return a list of all published topics
     * This info is just useful for some logging and should be implemented anyway, if not relevant
     * @return A list of all Mqtt Published Topics
     */
    fun getMqttPublishTopics(): ArrayList<Topic>

    /**
     * close will get called at the end of the lifecycle of the PluginManager
     * This function can be safely used to clean up and unsubscribe to topics.
     * @param handler A handler to a already initialized Mqtt5Client
     */
    fun close(handler: Mqtt5Client)

}
