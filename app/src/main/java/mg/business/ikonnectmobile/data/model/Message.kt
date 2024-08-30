// Message.kt
package mg.business.ikonnectmobile.data.model

data class Message(
    val id: String,
    val threadId: Int,
    val senderAddress: String,
    val body: String,
    val date: Long,
    val dateSent: Long,
    val isRead: Boolean,  // Use Boolean for readability
    val status: Int,
    val type: Int,
    val isSeen: Boolean // Use Boolean for readability
)
