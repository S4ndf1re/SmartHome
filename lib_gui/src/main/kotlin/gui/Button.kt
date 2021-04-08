package gui

import kotlinx.serialization.Serializable

@Serializable
class Button(override var name: String) : Child, Clickable, Textable {
    override var onClick: String = ""
    override var text: String = ""
    override var onClickMsg: String = ""
}