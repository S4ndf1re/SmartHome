package gui

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Button Structure
 */
@Serializable
class Button(override var name: String) : Child, Clickable, Textable {
    override var onClickRequest: String = ""
    override var text: String = ""

    @Transient
    override var onClick: (userId: String) -> Unit = {}
}