import com.github.s4ndf1re.DummyLogger
import com.github.s4ndf1re.LogLevel
import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.datatypes.MqttQos
import kotlinx.coroutines.delay
import org.ktorm.database.Database
import plugin.PluginSystem
import plugin.implementations.controller.ControllerCreator
import plugin.implementations.plugin.PluginCreator

suspend fun main() {
    val logger = DummyLogger("Backend System", LogLevel.DEBUG)
    //delay(30000)

    val config = Config.loadConfig()
    val system = PluginSystem.loadFromDir("plugins", PluginCreator(), logger.createNode("Plugin loading"))
    val controllers = PluginSystem.loadFromDir(
        "controller",
        ControllerCreator(system.pluginList.toMap()),
        logger.createNode("Controller Loader")
    )

    try {
        val client = MqttClient.builder()
            .useMqttVersion3()
            .automaticReconnectWithDefaultConfig()
            .willPublish().topic("backend/status/active")
            .qos(MqttQos.AT_LEAST_ONCE)
            .payload("false".toByteArray())
            .retain(true).applyWillPublish()
            .identifier(config.mqtt.identifier)
            .simpleAuth().username(config.mqtt.username).password(config.mqtt.password.toByteArray()).applySimpleAuth()
            .serverHost(config.mqtt.hostname).serverPort(config.mqtt.port)
            .build()
        client.toBlocking().connectWith().cleanSession(false).send()

        client.toBlocking().publishWith().topic("backend/status/active").payload("true".toByteArray()).retain(true)
            .send()


        val database = Database.connect(
            "jdbc:mysql://${config.sql.hostname}:${config.sql.port}/${config.sql.database}",
            user = config.sql.username,
            password = config.sql.password
        )


        system.start(client, database, logger.createNode("Plugins"))
        controllers.start(client, database, logger.createNode("Controllers"))

        while (true) {
            delay(5000)
        }

    } catch (e: Exception) {
        e.printStackTrace()
        system.stop()
        controllers.stop()
    }

    config.save()
}
