import com.hivemq.client.mqtt.MqttClient
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import plugin.ESP8266Safedoor
import plugin.PluginSystem

fun main() {
    val system = PluginSystem.create {
        this["safedoor01"] = ESP8266Safedoor()
        this["safedoor02"] = ESP8266Safedoor()
    }

    val client = MqttClient.builder().useMqttVersion5().identifier("mqtt_backend").serverHost("localhost").build()
    client.toBlocking().connect()
    system.start(client)
    runBlocking {
        delay(5000)
    }
    system.stop(client)
    client.toBlocking().disconnect()
}
