package gui

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
/**
 * Checkbox defines a specific Component that can be checked or not checked.
 * @author S4ndf1re
 */
class Checkbox(override var name: String) : Child, Textable, OnOffState {
    override var text: String = ""
    override var onOffStateRequest: String = ""
    override var onOnStateRequest: String = ""
    override var onGetStateRequest: String = ""

    @Transient
    override var getCurrent: (userId: String) -> Boolean = { this.state }

    @Transient
    override var onOnState: (userId: String) -> Unit = { this.state = true }

    @Transient
    override var onOffState: (userId: String) -> Unit = { this.state = false }

    /**
     * [state] describes the internal state of [Checkbox].
     */
    @Transient
    var state: Boolean = false
}
