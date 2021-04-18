package gui

import kotlinx.serialization.encodeToString
import org.junit.Test
import kotlin.test.assertEquals

class GuiTest {

    @Test
    fun test_01() {
        val gui = Gui.create {
            add("root") {
                hbox("hbox1") {
                    vbox("vbox1") {
                        button("button1") {
                            text = "Button1"
                            onClick = "button1/click"
                        }
                        label("label2") {
                            text = "Label1"
                        }
                    }
                    vsplitter("vsplitter1") {
                    }
                    vbox("vbox2") {
                        label("label2") {
                        }
                        slider("slider1") {
                        }
                    }
                    form("form1") {
                        button("form_button1") {

                        }
                    }
                }
            }
        }
        val format = gui.getJsonDefault()
        val str = format.encodeToString(gui)
        println(str)
        assertEquals(str, """{
    "containers": [
        {
            "name": "root",
            "list": [
                {
                    "type": "gui.HBox",
                    "name": "hbox1",
                    "list": [
                        {
                            "type": "gui.VBox",
                            "name": "vbox1",
                            "list": [
                                {
                                    "type": "gui.Button",
                                    "name": "button1",
                                    "onClick": "button1/click",
                                    "text": "Button1",
                                    "onClickMsg": ""
                                },
                                {
                                    "type": "gui.Label",
                                    "name": "label2",
                                    "text": "Label1",
                                    "topic": ""
                                }
                            ]
                        },
                        {
                            "type": "gui.VSplitter",
                            "name": "vsplitter1"
                        },
                        {
                            "type": "gui.VBox",
                            "name": "vbox2",
                            "list": [
                                {
                                    "type": "gui.Label",
                                    "name": "label2",
                                    "text": "",
                                    "topic": ""
                                },
                                {
                                    "type": "gui.Slider",
                                    "name": "slider1",
                                    "min": 0.0,
                                    "max": 100.0,
                                    "step": 1.0
                                }
                            ]
                        },
                        {
                            "type": "gui.Form",
                            "name": "form1",
                            "list": [
                                {
                                    "type": "gui.Button",
                                    "name": "form_button1",
                                    "onClick": "",
                                    "text": "",
                                    "onClickMsg": ""
                                }
                            ],
                            "topic": ""
                        }
                    ]
                }
            ]
        }
    ]
}""".trimIndent())

    }


}