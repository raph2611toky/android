package mg.business.ikonnectmobile.data.model
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val nom: String,
    val password: String
)
