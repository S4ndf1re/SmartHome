package gui

import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class Data(override var name: String) : Child {
    /**
     * [updateFunctions] holds all functions that will get called after [update] got called.
     */
    @Transient
    var updateFunctions: MutableMap<String, MutableList<suspend (Child) -> Unit>> = mutableMapOf()

    /**
     * [data] contains the current [Child]
     */
    @Transient
    var data: MutableMap<String, Child> = mutableMapOf()

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
    fun update(user: String, data: Child) {
        this.data[user] = data
        this.updateFunctions[user]?.let { functions ->
            for (updateFunction in functions) {
                kotlin.runCatching { // Keeping programm flow alive on exception
                    runBlocking {
                        updateFunction(data)
                    }
                }
            }
        }
    }

    /**
     * [registerUpdateFunction] will register a new function into [updateFunctions]
     * @param [user]
     * @param [newUpdateFunction]
     */
    fun registerUpdateFunction(user: String, newUpdateFunction: suspend (Child) -> Unit) {
        if (this.updateFunctions[user] != null) {
            this.updateFunctions[user]!!.add(newUpdateFunction)
        } else {
            this.updateFunctions[user] = mutableListOf(newUpdateFunction)
        }
    }

    /**
     * [unregisterUpdateFunction] removes an already registered function from [updateFunctions]
     * @param [existingUpdateFunction] must exist with the same reference in [updateFunctions] to get removed.
     */
    fun unregisterUpdateFunction(user: String, existingUpdateFunction: suspend (Child) -> Unit) {
        this.updateFunctions[user]?.remove(existingUpdateFunction)
    }

    /**
     * [getState] will return the current [data]
     * @return The current data as a [Child]
     */
    fun getState(user: String): Child? {
        return this.data[user]
    }
}