import com.hivemq.client.mqtt.MqttClient
import com.hivemq.client.mqtt.datatypes.MqttQos
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import plugin.ESP8266Safedoor
import plugin.PluginSystem

fun main() {
    val system = PluginSystem.create {
        this["safedoor01"] = ESP8266Safedoor()
    }

    val client = MqttClient.builder()
        .useMqttVersion5()
        .willPublish().topic("backend/status/active")
        .qos(MqttQos.AT_LEAST_ONCE)
        .payload("false".toByteArray())
        .retain(true).applyWillPublish()
        .identifier("mqtt_backend")
        .serverHost("localhost")
        .build()
    client.toBlocking().connect()
    system.start(client)
    runBlocking {
        delay(5000)
    }

    client.toBlocking().publishWith().topic("backend/status/active").payload("true".toByteArray()).retain(true).send()

    system.stop(client)
    client.toBlocking().publishWith().topic("backend/status/active").payload("false".toByteArray()).retain(true).send()
    client.toBlocking().disconnect()
}
