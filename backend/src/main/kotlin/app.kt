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
            .useMqttVersion3()
            .willPublish().topic("backend/status/active")
            .qos(MqttQos.AT_LEAST_ONCE)
            .payload("false".toByteArray())
            .retain(true).applyWillPublish()
            .identifier(config.mqtt.identifier)
            .simpleAuth().username(config.mqtt.username).password(config.mqtt.password.toByteArray()).applySimpleAuth()
            .serverHost(config.mqtt.hostname).serverPort(config.mqtt.port)
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
        controllers.stop(client)
        client.toBlocking().publishWith().topic("backend/status/active").payload("false".toByteArray()).retain(true)
            .send()
        client.toBlocking().disconnect()

    } catch (e: Exception) {

        e.printStackTrace()
    }

    config.save()
}
