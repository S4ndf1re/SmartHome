package gui

interface OnOffState {
    var onOffStateRequest: String
    var onOnStateRequest: String
    var onGetStateRequest: String

    /**
     * String as the argument is the user id from the ktor server
     */
    var onOnState: (userId: String) -> Unit
    var onOffState: (userId: String) -> Unit
    var getCurrent: (userId: String) -> Boolean
}