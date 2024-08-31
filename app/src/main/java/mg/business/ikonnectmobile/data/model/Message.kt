// Message.kt
package mg.business.ikonnectmobile.data.model

data class Message(
    val id: String,
    val threadId: Int,
    val senderAddress: String,
    val body: String,
    val date: Long,
    val dateSent: Long,
    val isRead: Boolean,
    val status: Int,
    val type: Int,
    val isSeen: Boolean,
    val recipientIds: List<String>?,
    val isFromMe: Boolean
)
