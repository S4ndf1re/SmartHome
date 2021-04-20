package gui

class Checkbox(override var name: String) : Child, Textable, Clickable {
    override var text: String = ""
    override var onClick: () -> Unit = {}
}