package gui

import kotlinx.serialization.Serializable

@Serializable
class Alert(override var name: String) : Child {
    var message: String = ""
}