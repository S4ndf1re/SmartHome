package gui


class Form(override var name: String) : Child, Widget {
    override var list: MutableList<Child> = mutableListOf()
    var onSend: suspend (values: String) -> Unit = {}
}