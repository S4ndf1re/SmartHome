package gui

/**
 * Should only be send as Data<Image>
 */
class Image(override var name: String) : Child {
    var width: Int = 100
    var height: Int = 100
    var data: ByteArray = ByteArray(0)
}