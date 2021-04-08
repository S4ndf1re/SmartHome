package gui

import kotlinx.serialization.Serializable

@Serializable
class Checkbox(override var name: String) : Child, Textable, Clickable {
    override var text: String = ""
    override var onClick: String = ""
    override var onClickMsg: String = ""
}