package gui

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

@Serializable
class Gui() {

    private val containers = arrayListOf<Container>()

    companion object Factory {
        fun create(f: Gui.() -> Unit): Gui {
            val gui = Gui()
            gui.f()
            return gui
        }
    }

    fun add(name: String, f: Container.() -> Unit) {
        val cont = Container(name)
        cont.f()
        this.containers.add(cont)
    }

    private fun getSerializationsModule(): SerializersModule {
        return SerializersModule {
            polymorphic(Widget::class) {
                subclass(Container::class)
                subclass(HBox::class)
                subclass(VBox::class)
                subclass(Form::class)
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
                subclass(Checkbox::class)
                subclass(ColorField::class)
                subclass(Data::class)
                subclass(Form::class)
                subclass(Image::class)
                subclass(TextField::class)
            }
            polymorphic(Clickable::class) {
                subclass(Button::class)
                subclass(Checkbox::class)
                subclass(Checkbox::class)
            }
            polymorphic(Textable::class) {
                subclass(Button::class)
                subclass(Checkbox::class)
                subclass(Label::class)
            }
            polymorphic(MqttData::class) {
                subclass(Data::class)
                subclass(Form::class)
                subclass(Image::class)
                subclass(Label::class)
            }

        }
    }

    fun getJsonDefault(): Json {
        return Json {
            serializersModule = getSerializationsModule()
            prettyPrint = true
            encodeDefaults = true
        }
    }

}