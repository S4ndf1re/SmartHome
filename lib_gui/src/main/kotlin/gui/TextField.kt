package gui

import kotlinx.serialization.Serializable

@Serializable
class TextField(override var name: String) : Child, Textable, MqttData {
    override var topic: String = ""
    override var text: String = ""
}