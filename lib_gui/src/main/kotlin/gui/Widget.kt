package gui

interface Widget {
    var list: MutableList<Child>

    fun hbox(f: HBox.() -> Unit) {
        val hBox = HBox()
        hBox.f()
        this.list.add(hBox)
    }

    fun vbox(f: VBox.() -> Unit) {
        val vBox = VBox()
        vBox.f()
        this.list.add(vBox)
    }

    fun button(f: Button.() -> Unit) {
        val button = Button()
        button.f()
        this.list.add(button)
    }

    fun label(f: Label.() -> Unit) {
        val label = Label()
        label.f()
        this.list.add(label)
    }

    fun vsplitter(f: VSplitter.() -> Unit) {
        val splitter = VSplitter()
        splitter.f()
        this.list.add(splitter)
    }

    fun hsplitter(f: HSplitter.() -> Unit) {
        val splitter = HSplitter()
        splitter.f()
        this.list.add(splitter)
    }

    fun slider(f: Slider.() -> Unit) {
        val slider = Slider()
        slider.f()
        this.list.add(slider)
    }

    fun data(f: Data.() -> Unit) {
        val data = Data()
        data.f()
        this.list.add(data)
    }

    fun checkbox(f: Checkbox.() -> Unit) {
        val check = Checkbox()
        check.f()
        this.list.add(check)
    }

    fun image(f: Image.() -> Unit) {
        val img = Image()
        img.f()
        this.list.add(img)
    }

    fun colorfield(f: ColorField.() -> Unit) {
        val col = ColorField()
        col.f()
        this.list.add(col)
    }

}