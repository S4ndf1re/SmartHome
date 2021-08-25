package gui


import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * A [Button] can be pressed. When pressed it will trigger the [onClick] function
 * @author S4ndf1re
 */
@Serializable
class Button(override var name: String) : Child, Clickable, Textable {
    override var onClickRequest: String = ""

    override var text: String = ""

    @Transient
    override var onClick: (userId: String) -> Unit = {}
}