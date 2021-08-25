package gui

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class Data(override var name: String) : Child {
    /**
     * [updateFunctions] holds all functions that will get called after [update] got called.
     */
    @Transient
    var updateFunctions: MutableList<suspend (Child) -> Unit> = mutableListOf()

    /**
     * [data] contains the current [Child]
     */
    @Transient
    var data: Child? = null

    /**
     * [updateRequest] describes the path, that any protocol must call, in order to receive the current [data]
     */
    var updateRequest: String = ""

    /**
     * [updateSocket] describes the Socket, that any protocol must open, in order to receive live updates for [data] changes
     */
    var updateSocket: String = ""

    /**
     * [update] should get called, everytime the containing [data] should change and all [updateFunctions] should get called.
     * @param [data] the new data that will get set internally.
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
     * [registerUpdateFunction] will register a new function into [updateFunctions]
     * @param [newUpdateFunction]
     */
    fun registerUpdateFunction(newUpdateFunction: suspend (Child) -> Unit) {
        this.updateFunctions.add(newUpdateFunction)
    }

    /**
     * [unregisterUpdateFunction] removes an already registered function from [updateFunctions]
     * @param [existingUpdateFunction] must exist with the same reference in [updateFunctions] to get removed.
     */
    fun unregisterUpdateFunction(existingUpdateFunction: suspend (Child) -> Unit) {
        this.updateFunctions.remove(existingUpdateFunction)
    }

    /**
     * [getState] will return the current [data]
     * @return The current data as a [Child]
     */
    fun getState(): Child? {
        return this.data
    }
}