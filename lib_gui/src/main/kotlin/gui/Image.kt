package gui

class Image : Child, MqttData {
    override var topic: String = ""
    var width: Int = 100
    var height: Int = 100
}