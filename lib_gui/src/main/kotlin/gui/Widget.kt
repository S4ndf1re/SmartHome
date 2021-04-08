package gui

interface Widget {
    var list: MutableList<Child>

    fun hbox(name: String, f: HBox.() -> Unit) {
        val hBox = HBox(name)
        hBox.f()
        this.list.add(hBox)
    }

    fun vbox(name: String, f: VBox.() -> Unit) {
        val vBox = VBox(name)
        vBox.f()
        this.list.add(vBox)
    }

    fun button(name: String, f: Button.() -> Unit) {
        val button = Button(name)
        button.f()
        this.list.add(button)
    }

    fun label(name: String, f: Label.() -> Unit) {
        val label = Label(name)
        label.f()
        this.list.add(label)
    }

    fun vsplitter(name: String, f: VSplitter.() -> Unit) {
        val splitter = VSplitter(name)
        splitter.f()
        this.list.add(splitter)
    }

    fun hsplitter(name: String, f: HSplitter.() -> Unit) {
        val splitter = HSplitter(name)
        splitter.f()
        this.list.add(splitter)
    }

    fun slider(name: String, f: Slider.() -> Unit) {
        val slider = Slider(name)
        slider.f()
        this.list.add(slider)
    }

    fun data(name: String, f: Data.() -> Unit) {
        val data = Data(name)
        data.f()
        this.list.add(data)
    }

    fun checkbox(name: String, f: Checkbox.() -> Unit) {
        val check = Checkbox(name)
        check.f()
        this.list.add(check)
    }

    fun image(name: String, f: Image.() -> Unit) {
        val img = Image(name)
        img.f()
        this.list.add(img)
    }

    fun colorfield(name: String, f: ColorField.() -> Unit) {
        val col = ColorField(name)
        col.f()
        this.list.add(col)
    }

    fun form(name: String, f: Form.() -> Unit) {
        val form = Form(name)
        form.f()
        this.list.add(form)
    }

}