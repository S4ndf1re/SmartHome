package gui

import kotlinx.serialization.encodeToString
import org.junit.Test
import kotlin.test.assertEquals

class GuiTest {

    @Test
    fun test_01() {
        val gui = Gui.create {
            hbox {
                vbox {
                    button {
                        text = "Button1"
                        onClick = "button1/click"
                    }
                    label {
                        text = "Label1"
                    }
                }
                vsplitter {
                }
                vbox {
                    label {
                    }
                    slider {
                    }
                }
            }
        }
        val format = gui.getJsonDefault()
        val str = format.encodeToString(gui)
        //println(str)
        assertEquals(str, """{
    "child": {
        "type": "gui.Container",
        "list": [
            {
                "type": "gui.HBox",
                "list": [
                    {
                        "type": "gui.VBox",
                        "list": [
                            {
                                "type": "gui.Button",
                                "onClick": "button1/click",
                                "text": "Button1"
                            },
                            {
                                "type": "gui.Label",
                                "text": "Label1"
                            }
                        ]
                    },
                    {
                        "type": "gui.VSplitter"
                    },
                    {
                        "type": "gui.VBox",
                        "list": [
                            {
                                "type": "gui.Label"
                            },
                            {
                                "type": "gui.Slider"
                            }
                        ]
                    }
                ]
            }
        ]
    }
}""".trimIndent())

    }


}