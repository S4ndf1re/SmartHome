package gui

interface OnOffState {
    var onOffStateRequest: String
    var onOnStateRequest: String
    var onGetStateRequest: String

    var onOnState: () -> Unit
    var onOffState: () -> Unit
    var getCurrent: () -> Boolean
}