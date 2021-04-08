package gui

import kotlinx.serialization.Serializable

@Serializable
class Form(override var name: String) : Child, Widget, MqttData {
    override var list: MutableList<Child> = mutableListOf()
    override var topic: String = ""
}