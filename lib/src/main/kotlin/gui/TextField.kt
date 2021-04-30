package gui

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient


@Serializable
class TextField(override var name: String) : Child, Textable, TextInput {
    override var text: String = ""
    override var updateRequest: String = ""

    @Transient
    override var update: (text: String) -> Unit = {}

}