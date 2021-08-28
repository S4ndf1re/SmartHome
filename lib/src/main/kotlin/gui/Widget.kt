package gui

/**
 * A [Widget] is a [Child], that can have its own [Child]ren.
 * It is meant as a gui [Widget] like a html div or something similar.
 */
interface Widget {
    var list: MutableList<Child>

    fun hbox(name: String, f: HBox.() -> Unit): HBox {
        val hBox = HBox(name)
        hBox.f()
        this.list.add(hBox)
        return hBox
    }

    fun vbox(name: String, f: VBox.() -> Unit): VBox {
        val vBox = VBox(name)
        vBox.f()
        this.list.add(vBox)
        return vBox
    }

    fun button(name: String, f: Button.() -> Unit): Button {
        val button = Button(name)
        button.f()
        this.list.add(button)
        return button
    }

    fun label(name: String, f: Label.() -> Unit): Label {
        val label = Label(name)
        label.f()
        this.list.add(label)
        return label
    }

    fun vsplitter(name: String, f: VSplitter.() -> Unit): VSplitter {
        val splitter = VSplitter(name)
        splitter.f()
        this.list.add(splitter)
        return splitter
    }

    fun hsplitter(name: String, f: HSplitter.() -> Unit): HSplitter {
        val splitter = HSplitter(name)
        splitter.f()
        this.list.add(splitter)
        return splitter
    }

    fun slider(name: String, f: Slider.() -> Unit): Slider {
        val slider = Slider(name)
        slider.f()
        this.list.add(slider)
        return slider
    }

    fun data(name: String, f: Data.() -> Unit): Data {
        val data = Data(name)
        data.f()
        this.list.add(data)
        return data
    }

    fun checkbox(name: String, f: Checkbox.() -> Unit): Checkbox {
        val checkbox = Checkbox(name)
        checkbox.f()
        this.list.add(checkbox)
        return checkbox
    }

    fun image(name: String, f: Image.() -> Unit): Image {
        val img = Image(name)
        img.f()
        this.list.add(img)
        return img
    }

    fun colorfield(name: String, f: ColorField.() -> Unit): ColorField {
        val col = ColorField(name)
        col.f()
        this.list.add(col)
        return col
    }

    fun textfield(name: String, f: TextField.() -> Unit): TextField {
        val txt = TextField(name)
        txt.f()
        this.list.add(txt)
        return txt
    }

}