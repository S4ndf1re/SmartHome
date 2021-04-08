package gui

import kotlinx.serialization.Serializable

@Serializable
class Container(override var name: String) : Child, Widget {
    override var list: MutableList<Child> = mutableListOf()
}