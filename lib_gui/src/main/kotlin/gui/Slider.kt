package gui

import kotlinx.serialization.Serializable

@Serializable
class Slider : Child {
    var min: Float = 0.0F
    var max: Float = 100.0F
    var step: Float = 1.0F
}