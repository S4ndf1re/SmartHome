package gui

class Container(override var name: String) : Child, Widget {
    override var list: MutableList<Child> = mutableListOf()

    companion object Factory {
        fun create(name: String, f: Container.() -> Unit): Container {
            val cont = Container(name)
            cont.f()
            return cont
        }
    }

}