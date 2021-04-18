package gui

/**
 * Any clickable widget
 */
interface Clickable {
    /**
     * The path on were to write [onClickMsg]
     */
    var onClick: String

    /**
     * The message that should get written when a click event occures
     */
    var onClickMsg: String
}