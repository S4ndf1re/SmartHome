package gui

/**
 * [OnOffState] is an interface of every [Widget] that must implement an on or off state.
 */
interface OnOffState {
    /**
     * [onOffStateRequest] describes the path, that must get called by any protocol in order to trigger [onOffState].
     */
    var onOffStateRequest: String

    /**
     * [onOnStateRequest] describes the path, that must get called by any protocol in order to trigger [onOnState].
     */
    var onOnStateRequest: String

    /**
     * [onGetStateRequest] describes the path, that must get called in order to retrieve the current state of the checkbox.
     * The current value must get saved either by the creator of [OnOffState] or by the [OnOffState]-Implementation itself (see [Checkbox]).
     */
    var onGetStateRequest: String

    /**
     * [onOnState] defines the function that will get called everytime the state of the checkbox is set to 'ON'.
     * By default, this function will set the internal state to 'true'.
     */
    var onOnState: suspend (userId: String) -> Unit

    /**
     * [onOffState] defines the function that will get called everytime the state of the checkbox is set to 'OFF'.
     * By default, this function will set the internal state to 'false'.
     */
    var onOffState: suspend (userId: String) -> Unit

    /**
     * [getCurrent] will get called everytime the current state must be fetched.
     * By default, it will return its internal state.
     */
    var getCurrent: suspend (userId: String) -> Boolean
}