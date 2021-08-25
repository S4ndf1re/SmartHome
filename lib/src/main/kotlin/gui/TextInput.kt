package gui

/**
 * [TextInput] is for every [Child] that should implement text input
 */
interface TextInput {

    /**
     * [update] is the function that will get called at text input from the frontend.
     */
    var update: (userId: String, text: String) -> Unit

    /**
     * [updateRequest] is the path, that any protocol has to call, in order to execute [update]
     */
    var updateRequest: String

}