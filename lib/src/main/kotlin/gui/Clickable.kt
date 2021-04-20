package gui

/**
 * Any clickable widget
 */
interface Clickable {
    /**
     * onClick is a callback function that gets called after the controller registeres a button click
     */
    var onClick: () -> Unit
}