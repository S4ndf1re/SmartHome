package gui

import kotlinx.serialization.Serializable

@Serializable
/**
 * [Container] implements the default [Widget] that should display first in any frontend.
 */
class Container(override var name: String) : Child, Widget {
    /**
     * [list] holds all [Child]ren that will get displayed by the [Widget].
     */
    override var list: MutableList<Child> = mutableListOf()

    companion object Factory {
        /**
         * [create]
         * @param [name] name of the [Widget]
         * @param [f] configuration function on [Container]
         * @return a newly created [Container], configured by [f]
         */
        fun create(name: String, f: Container.() -> Unit): Container {
            val cont = Container(name)
            cont.f()
            return cont
        }
    }

}