package gui

class VBox(override var name: String) : Child, Widget {
    override var list: MutableList<Child> = mutableListOf()
}