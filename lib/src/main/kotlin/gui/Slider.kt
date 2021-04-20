package gui

class Slider(override var name: String) : Child {
    var min: Float = 0.0F
    var max: Float = 100.0F
    var step: Float = 1.0F
    var onUpdate: (value: Float) -> Unit = {}
}