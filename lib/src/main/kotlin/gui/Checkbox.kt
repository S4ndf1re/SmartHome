package gui

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class Checkbox(override var name: String) : Child, Textable, OnOffState {
    override var text: String = ""
    override var onOffStateRequest: String = ""
    override var onOnStateRequest: String = ""
    override var onGetStateRequest: String = ""

    @Transient
    override var getCurrent: () -> Boolean = { false }

    @Transient
    override var onOnState: () -> Unit = {}

    @Transient
    override var onOffState: () -> Unit = {}


}