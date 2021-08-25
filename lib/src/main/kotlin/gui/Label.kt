package gui

/**
 * If updatable is needed, it should be wrapped in Data<Label>
 */
class Label(override var name: String) : Child, Textable {
    override var text: String = ""
}