package gui

/**
 * Button Structure
 */
class Button(override var name: String) : Child, Clickable, Textable {
    override var onClick: () -> Unit = {}
    override var text: String = ""
}