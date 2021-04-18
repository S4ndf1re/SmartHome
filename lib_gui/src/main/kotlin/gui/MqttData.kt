package gui

/**
 * Any kind of data expected over mqtt. The frontend does not need to implement mqtt,
 * but the controller implementation has to. The frontend only has to communicate to the controller
 */
interface MqttData {
    /**
     * The topic on where to publish the data (as json)
     */
    var topic: String
}