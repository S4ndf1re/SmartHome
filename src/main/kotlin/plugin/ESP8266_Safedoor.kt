package plugin

import com.hivemq.client.mqtt.mqtt5.Mqtt5Client

class ESP8266Safedoor : IPlugin {
    override fun init(handler: Mqtt5Client) {
        handler.toAsync().subscribeWith().topicFilter("safedoor/01").callback {
            println(it.payload.get().asCharBuffer().toString())
        }.send()
    }

    override fun close(handler: Mqtt5Client) {
        handler.toAsync().unsubscribeWith().addTopicFilter("safedoor/01").send()
    }
}