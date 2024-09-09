package mg.business.ikonnectmobile.data.model
import kotlinx.serialization.Serializable

@Serializable
data class Utilisateur(
    val id: Int? = null,
    val nom: String = "",
    val password: String = "",
    val createdAt: String? = null,
    val lastLogin: String? = null
)
