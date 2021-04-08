package gui

import kotlinx.serialization.Serializable

@Serializable
class Label(override var name: String) : Child, Textable, MqttData {
    override var text: String = ""
    override var topic: String = ""
}