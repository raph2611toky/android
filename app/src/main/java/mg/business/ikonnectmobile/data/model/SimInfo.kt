package mg.business.ikonnectmobile.data.model
import kotlinx.serialization.Serializable

@Serializable
data class SimInfo(
    val simSlotIndex: Int,
    val carrierName: String,
    val countryIso: String,
    val displayName: String,
    val iccId: String,
    val number: String?,
    val subscriptionId: Int,
    val isActive: Boolean
)