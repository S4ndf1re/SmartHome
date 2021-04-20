package gui

class Data<T>(override var name: String) : Child where T : ToJson {
    var data: T? = null
    private var updateList: ArrayList<(T) -> Unit> = arrayListOf()

    fun update(data: T) {
        this.data = data
        for (f in updateList) {
            f(data)
        }
    }
}