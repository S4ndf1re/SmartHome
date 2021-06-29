package gui

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class Data<T>(override var name: String) : Child where T : ToJson {
    /**
     * updateFct should only be filled by the controlling backend
     */
    @Transient
    var updateFcts: MutableMap<String, (T) -> Unit> = mutableMapOf()

    @Transient
    var data: T? = null

    var updateRequest: String = ""
    var updateSocket: String = ""

    /**
     * This function is meant to be called by the updating caller.
     */
    fun update(data: T) {
        this.data = data
        for (updateFct in updateFcts.values) {
            updateFct(data)
        }
    }

    fun getState(): T? {
        return this.data
    }
}