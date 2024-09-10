package mg.business.ikonnectmobile.data.model
import kotlinx.serialization.Serializable

@Serializable
data class RefreshToken(
    val id_refresh: Int? = null,
    val user_id: Int? = null,
    val refresh_token: String = "",
    val expiration: String? = null,
    val already_used: Boolean? = null
)
