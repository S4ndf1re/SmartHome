package gui

import kotlinx.serialization.Serializable

@Serializable
class Checkbox : Child, Textable, Clickable {
    override var text: String = ""
    override var onClick: String = ""
}