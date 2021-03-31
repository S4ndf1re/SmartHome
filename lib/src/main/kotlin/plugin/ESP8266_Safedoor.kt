package plugin

import com.hivemq.client.mqtt.mqtt5.Mqtt5Client
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5Publish

class ESP8266Safedoor : IPlugin {
    override fun init(handler: Mqtt5Client) {
        handler.toAsync().subscribeWith().topicFilter("safedoor/01").callback {
            if (it != null) {
                this.handle(it)
            }
        }.send()
    }

    override fun close(handler: Mqtt5Client) {
        handler.toAsync().unsubscribeWith().addTopicFilter("safedoor/01").send()
    }

    private fun handle(data: Mqtt5Publish) {
        println("$data")
    }

    override fun getHttpHandles(): Map<String, () -> String> {
        TODO("Not yet implemented")
    }

    override fun getMqttPublishTopics(): ArrayList<String> {
        TODO("Not yet implemented")
    }

    override fun getMqttSubscriptionsTopics(): ArrayList<String> {
        TODO("Not yet implemented")
    }
}