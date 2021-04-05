package gui

import kotlinx.serialization.Serializable

@Serializable
class Button : Child, Clickable, Textable {
    override var onClick: String = ""
    override var text: String = ""
}