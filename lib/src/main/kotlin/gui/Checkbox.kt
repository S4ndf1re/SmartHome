package gui

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class Checkbox(override var name: String) : Child, Textable, Clickable {
    override var text: String = ""
    override var onClickRequest: String = ""

    @Transient
    override var onClick: () -> Unit = {}

}