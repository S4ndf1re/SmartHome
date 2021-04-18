import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.datatypes.MqttQos
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import plugin.PluginSystem
import plugin.implementations.controller.ControllerCreator
import plugin.implementations.plugin.PluginCreator


fun main() {

    val config = Config.loadConfig()
    val system = PluginSystem.loadFromDir("plugins", PluginCreator())
    val controllers = PluginSystem.loadFromDir("controller", ControllerCreator(system.pluginList.toMap()))


    try {
        val client = MqttClient.builder()
            .useMqttVersion5()
            .willPublish().topic("backend/status/active")
            .qos(MqttQos.AT_LEAST_ONCE)
            .payload("false".toByteArray())
            .retain(true).applyWillPublish()
            .identifier(config.identifier)
            .simpleAuth().username(config.username).password(config.password.toByteArray()).applySimpleAuth()
            .serverHost(config.hostname)
            .build()
        client.toBlocking().connect()

        client.toBlocking().publishWith().topic("backend/status/active").payload("true".toByteArray()).retain(true)
            .send()

        system.start(client)
        controllers.start(client)

        runBlocking {
            delay(5000)
        }

        system.stop(client)
        client.toBlocking().publishWith().topic("backend/status/active").payload("false".toByteArray()).retain(true)
            .send()
        client.toBlocking().disconnect()

    } catch (e: Exception) {

    }

    config.save()
}
