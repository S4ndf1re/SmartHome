package gui

import kotlinx.serialization.Serializable

@Serializable
class HBox : Child, Widget {
    override var list: MutableList<Child> = mutableListOf()
}