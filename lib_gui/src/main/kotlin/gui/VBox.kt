package gui

import kotlinx.serialization.Serializable

@Serializable
class VBox(override var name: String) : Child, Widget {
    override var list: MutableList<Child> = mutableListOf()
}