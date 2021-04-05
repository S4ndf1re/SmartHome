package gui

import kotlinx.serialization.Serializable

@Serializable
class Label : Child, Textable {
    override var text: String = ""
}