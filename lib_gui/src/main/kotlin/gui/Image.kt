package gui

import kotlinx.serialization.Serializable

@Serializable
class Image(override var name: String) : Child, MqttData {
    override var topic: String = ""
    var width: Int = 100
    var height: Int = 100
}