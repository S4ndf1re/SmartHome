package gui

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

@Serializable
class Gui(val child: Widget) {

    companion object Factory {
        fun create(f: Container.() -> Unit): Gui {
            val cont = Container()
            cont.f()
            return Gui(cont)
        }
    }

    private fun getSerializationsModule(): SerializersModule {
        return SerializersModule {
            polymorphic(Widget::class) {
                subclass(Container::class)
                subclass(HBox::class)
                subclass(VBox::class)
            }
            polymorphic(Child::class) {
                subclass(Button::class)
                subclass(Container::class)
                subclass(HBox::class)
                subclass(HSplitter::class)
                subclass(Label::class)
                subclass(VBox::class)
                subclass(VSplitter::class)
                subclass(Slider::class)
            }
        }
    }

    fun getJsonDefault(): Json {
        return Json {
            serializersModule = getSerializationsModule()
            prettyPrint = true
        }
    }

}