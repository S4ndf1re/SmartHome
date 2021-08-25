package gui

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


/**
 * [TextField] is a input field for text
 */
@Serializable
class TextField(override var name: String) : Child, Textable, TextInput {
    override var text: String = ""
    override var updateRequest: String = ""

    @Transient
    override var update: (userId: String, text: String) -> Unit = { _: String, _: String -> }

}