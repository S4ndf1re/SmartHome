package gui

interface TextInput {

    var update: (userId: String, text: String) -> Unit
    var updateRequest: String

}