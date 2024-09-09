package mg.business.ikonnectmobile.data.model
import kotlinx.serialization.Serializable

@Serializable
data class Utilisateur(
    val id: Int? = null,
    val nom: String = "",
    val email: String = "",
    val password: String = "",
    val cin: String = "",
    val createdAt: String? = null,
    val lastLogin: String? = null
)
