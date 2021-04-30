package gui

interface TextInput {

    var update: (text: String) -> Unit
    var updateRequest: String

}