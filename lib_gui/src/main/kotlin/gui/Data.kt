package gui

import kotlinx.serialization.Serializable

@Serializable
class Data(override var name: String) : Child, MqttData {
    override var topic: String = ""
}