package mg.business.ikonnectmobile.data.model
import kotlinx.serialization.Serializable

@Serializable
data class PayementVerifyRequest(
    val reference: String,
    val numero: String,
    val date: String
)
