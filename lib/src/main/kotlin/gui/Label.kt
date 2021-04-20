package gui

/**
 * If updatable is needed, it should be wrapped in Data<Label>
 */
class Label(override var name: String) : Child, Textable, ToJson {
    override var text: String = ""

    override fun toJson(): String {
        TODO("Not yet implemented")
    }
}