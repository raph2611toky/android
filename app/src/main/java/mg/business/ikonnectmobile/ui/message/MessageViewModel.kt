// MessageViewModel.kt
package mg.business.ikonnectmobile.ui.message

import android.app.Application
import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import mg.business.ikonnectmobile.data.model.Message

class MessageViewModel(application: Application) : AndroidViewModel(application) {

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> get() = _messages

    init {
        loadMessages()
    }

    private fun Cursor.safeGetInt(columnName: String): Int {
        val index = getColumnIndex(columnName)
        return if (index != -1) getInt(index) else 0
    }

    private fun Cursor.safeGetLong(columnName: String): Long {
        val index = getColumnIndex(columnName)
        return if (index != -1) getLong(index) else 0L
    }

    private fun Cursor.safeGetString(columnName: String): String? {
        val index = getColumnIndex(columnName)
        return if (index != -1) getString(index) else null
    }

    private fun loadMessages() {
        val contentResolver: ContentResolver = getApplication<Application>().contentResolver
        val uri = Uri.parse("content://sms/inbox")
        val selection = "thread_id = ?"
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, "date ASC")

        val messagesList = mutableListOf<Message>()
        cursor?.use {

            while (it.moveToNext()) {
                val id = cursor.safeGetString("_id")
                val body = cursor.safeGetString("body")
                val date = cursor.safeGetLong("date")
                val recipientIdsString = cursor.safeGetString("recipientIds")
                val isFromMe = cursor.safeGetString("type") == "2"

                messagesList.add(
                    Message(
                        id = id.toString(),
                        threadId = cursor.safeGetInt("thread_id"),
                        senderAddress = cursor.safeGetString("address").toString(),
                        body = body.toString(),
                        date = date,
                        status = cursor.safeGetInt("status"),
                        isRead = cursor.safeGetInt("read") == 1,
                        type = cursor.safeGetInt("type"),
                        dateSent = cursor.safeGetLong("date_sent"),
                        isSeen = cursor.safeGetInt("seen") == 1,
                        recipientIds = recipientIdsString?.split(","),
                        isFromMe = isFromMe
                    )
                )
            }
        }

        _messages.value = messagesList
    }

    fun addMessage(message: Message) {
        val currentMessages = _messages.value?.toMutableList() ?: mutableListOf()
        currentMessages.add(0, message)
        _messages.value = currentMessages
    }
}
