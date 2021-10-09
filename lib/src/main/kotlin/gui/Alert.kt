package gui

import kotlinx.serialization.Serializable

@Serializable
class Alert(override var name: String, var message: String) : Child