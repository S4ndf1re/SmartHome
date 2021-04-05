package gui

import kotlinx.serialization.Serializable

@Serializable
class VBox : Child, Widget {
    override var list: MutableList<Child> = mutableListOf()
}