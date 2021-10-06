package gui

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

/**
 * [Gui] is the complete frontend container
 */
@Serializable
class Gui() {

    /**
     * [containers] is the list of all [Container]s created by underlying [plugin.implementations.plugin.IPlugin]
     */
    private val containers = arrayListOf<Container>()

    companion object Factory {
        /**
         * [create] will generate a new [Gui] with a configuration function
         * @param [f] is the used configuration function
         * @return A new [Gui]
         */
        fun create(f: Gui.() -> Unit): Gui {
            val gui = Gui()
            gui.f()
            return gui
        }

        /**
         * [getSerializationsModule] will create a complete list of all JSON-Inheritance. This is needed because Sealed Class
         * is unusable in this project structure.
         * @return A complete inheritance list as a [SerializersModule]
         */
        private fun getSerializationsModule(): SerializersModule {
            return SerializersModule {
                polymorphic(Widget::class) {
                    subclass(Container::class)
                    //subclass(HBox::class)
                    //subclass(VBox::class)
                }
                polymorphic(Child::class) {
                    subclass(Button::class)
                    subclass(Container::class)
                    // subclass(HBox::class)
                    // subclass(HSplitter::class)
                    // subclass(Label::class)
                    // subclass(VBox::class)
                    // subclass(VSplitter::class)
                    // subclass(Slider::class)
                    subclass(Checkbox::class)
                    // subclass(ColorField::class)
                    subclass(Data::class)
                    // subclass(Form::class)
                    // subclass(Image::class)
                    subclass(TextField::class)
                    subclass(Alert::class)
                }
            }
        }

        /**
         * [getJsonDefault] generates a new [Json]-engine using the module from [getSerializationsModule]
         * @return A new [Json]-Engine
         */
        fun getJsonDefault(): Json {
            return Json {
                serializersModule = getSerializationsModule()
                prettyPrint = true
                encodeDefaults = true
            }
        }
    }

    /**
     * [add] adds a [Container] to [containers]
     * @param [cont] the newly added [Container]
     */
    fun add(cont: Container) {
        this.containers.add(cont)
    }


}