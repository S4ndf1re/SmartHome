package gui

/**
 * Should only be send as Data<Image>
 */
class Image(override var name: String) : Child, ToJson {
    var width: Int = 100
    var height: Int = 100
    var data: ByteArray = ByteArray(0)

    override fun toJson(): String {
        TODO("Not yet implemented")
    }
}