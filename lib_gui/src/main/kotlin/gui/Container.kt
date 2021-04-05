package gui

import kotlinx.serialization.Serializable

@Serializable
class Container : Child, Widget {
    override var list: MutableList<Child> = mutableListOf()
}