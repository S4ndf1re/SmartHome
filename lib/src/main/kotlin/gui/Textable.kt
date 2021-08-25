package gui

/**
 * Anything that can have a text
 */
interface Textable {
    /**
     * The [text] given by either the frontend or the plugin. This text describes what text to show the user.
     */
    var text: String
}