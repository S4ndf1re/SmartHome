package gui

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class Data(override var name: String) : Child {
    /**
     * updateFunctions should only be filled by the controlling backend
     */
    @Transient
    var updateFunctions: MutableList<suspend (Child) -> Unit> = mutableListOf()

    @Transient
    var data: Child? = null

    /**
     * updateRequest is the path on where to fetch data
     */
    var updateRequest: String = ""

    /**
     * updateSocket is the corresponding websocket connection to fetch data.
     */
    var updateSocket: String = ""

    /**
     * This function is meant to be called by the updating caller.
     */
    fun update(data: Child) {
        this.data = data
        for (updateFunction in updateFunctions) {
            suspend {
                try {
                    updateFunction(data)
                } catch (exception: Exception) {
                    // Keep Program flow alive
                }
            }
        }
    }

    /**
     * registerUpdateFunction will register another update function.
     * This will get called everytime some thread or object will call update.
     */
    fun registerUpdateFunction(newUpdateFunction: suspend (Child) -> Unit) {
        this.updateFunctions.add(newUpdateFunction)
    }

    /**
     * unregisterUpdateFunction will remove an update function from the internal list of functions.
     * See registerUpdateFunction for reference.
     */
    fun unregisterUpdateFunction(existingUpdateFunction: suspend (Child) -> Unit) {
        this.updateFunctions.remove(existingUpdateFunction)
    }

    /**
     * getState will return the current Object in this class
     */
    fun getState(): Child? {
        return this.data
    }
}