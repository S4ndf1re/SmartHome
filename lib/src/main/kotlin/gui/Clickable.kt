package gui

/**
 * Any clickable widget
 */
interface Clickable {
    /**
     * onClick is a callback function that gets called after the controller registers a button click
     * It takes a user id as string argument
     */
    var onClick: (userId: String) -> Unit

    var onClickRequest: String
}