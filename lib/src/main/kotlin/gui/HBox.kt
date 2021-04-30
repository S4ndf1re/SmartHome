package gui


class HBox(override var name: String) : Child, Widget {
    override var list: MutableList<Child> = mutableListOf()
}