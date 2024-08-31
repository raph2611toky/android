package mg.business.ikonnectmobile.data.model

data class Discussion(
    val threadId: Int,
    val date: Long,
    val messageCount: Int,
    val recipientIds: List<String>?,
    val snippet: String,
    val isRead: Boolean,
    val type: Int,
    val error: Int,
    var messages: List<Message> = emptyList(),
    var lastMessage: Message? = null
)
