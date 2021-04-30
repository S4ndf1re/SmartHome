import com.hivemq.client.mqtt.mqtt3.Mqtt3Client
import gui.Container
import plugin.Topic
import plugin.implementations.plugin.IPlugin
import java.nio.charset.StandardCharsets

class Plugin : IPlugin {
    override fun init(handler: Mqtt3Client): Boolean {
        handler.toAsync().subscribeWith().topicFilter("test/plugin1").callback {
            val s = StandardCharsets.UTF_8.decode(it.payload.get())
            println("Payload: $s")
        }.send()
        println("This is just a test plugin. Do not consider to use this in your system later on")
        return true
    }

    override fun close(handler: Mqtt3Client) {
        handler.toAsync().unsubscribeWith().topicFilter("test/plugin1").send()
        println("This Plugin will now get closed and cleaned up afterwards")
    }

    override fun getMqttPublishTopics(): ArrayList<Topic> {
        return arrayListOf()
    }

    override fun getContainer(path: String): Container {
        return Container.create("name") {

            button("btn1") {
                text = "Click me"
                onClick = {
                    println("You clicked me")
                }
            }

        }
    }

    override fun getMqttSubscriptionsTopics(): ArrayList<Topic> {
        return arrayListOf("test/plugin1")
    }
}