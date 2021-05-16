import com.hivemq.client.mqtt.mqtt3.Mqtt3Client
import gui.Container
import plugin.Topic
import plugin.implementations.plugin.IPlugin
import java.nio.charset.StandardCharsets

class Plugin : IPlugin {

    private var state: Boolean = false

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

    override fun getContainers(): List<Container> {
        return listOf(Container.create("Doorlock1") {
            button("check1") {
                text = "Check"
                onClick = {
                    println("Check")
                }
            }
            button("auth1") {
                text = "Auth"
                onClick = {
                    println("Auth")
                }
            }
        },
            Container.create("Doorlock2") {
                button("check2") {
                    text = "Check"
                    onClick = {
                        println("Check")
                    }
                }
                button("auth2") {
                    text = "Auth"
                    onClick = {
                        println("Auth")
                    }
                }
                checkbox("check1") {
                    text = "Check me"
                    onOffState = {
                        println("Off")
                        state = false
                    }
                    onOnState = {
                        println("on")
                        state = true
                    }
                    getCurrent = { getState() }
                }
                textfield("text1") {
                    text = "Textfield"
                    update = {
                        println("Received: $it")
                    }
                }
            })
    }

    private fun getState(): Boolean {
        return this.state
    }

    override fun getMqttSubscriptionsTopics(): ArrayList<Topic> {
        return arrayListOf("test/plugin1")
    }
}