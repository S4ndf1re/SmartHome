package gui


class TextField(override var name: String) : Child, Textable, ToJson {
    override var text: String = ""

    override fun toJson(): String {
        TODO("Not yet implemented")
    }
}