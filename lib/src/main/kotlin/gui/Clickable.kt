package gui

/**
 * Any clickable [Widget]
 */
interface Clickable {
    /**
     * [onClick] is a callback function that gets called after the controller registers a click on the [Widget]
     * It takes a user id as [String] argument
     */
    var onClick: suspend (userId: String) -> Unit

    /**
     * [onClickRequest] defines the path, that has to get called by any protocol. When called, it will trigger [onClick]
     */
    var onClickRequest: String
}