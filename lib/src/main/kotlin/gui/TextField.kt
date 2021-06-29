package gui

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
class TextField(override var name: String) : Child, Textable, TextInput {
    override var text: String = ""

    // Should send data with post request and as json: { "text": "content here"}
    override var updateRequest: String = ""

    @Transient
    override var update: (userId: String, text: String) -> Unit = { _: String, _: String -> }

}