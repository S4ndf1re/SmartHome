import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.datatypes.MqttQos
import org.ktorm.database.Database
import plugin.PluginSystem
import plugin.implementations.controller.ControllerCreator
import plugin.implementations.plugin.PluginCreator
import sun.misc.Signal
import java.util.*

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


        val database = Database.connect("jdbc:mysql://${config.sql.hostname}:${config.sql.port}/${config.sql.database}",
            user = config.sql.username,
            password = config.sql.password)

        system.start(client, database)
        controllers.start(client, database)

        val shutdown = {
            system.stop()
            controllers.stop()
            client.toBlocking().publishWith().topic("backend/status/active").payload("false".toByteArray())
                .retain(true)
                .send()
            client.toBlocking().disconnect()
        }

        Signal.handle(Signal("INT")) {
            shutdown()
        }

        val scanner = Scanner(System.`in`)

        while (true) {
            print("> ")
            val word = scanner.next()
            if (word == "exit") {
                break
            }
            println()
        }

        shutdown()

    } catch (e: Exception) {

        e.printStackTrace()
    }

    config.save()
}
