import kotlinx.serialization.Serializable

@Serializable
data class Payload(val uid: String, val data: String)