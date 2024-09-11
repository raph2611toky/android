package mg.business.ikonnectmobile.ui.discussion

import android.app.Application
import android.content.ContentResolver
import android.database.Cursor
import android.database.sqlite.SQLiteException
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mg.business.ikonnectmobile.data.model.Discussion
import mg.business.ikonnectmobile.data.model.Message
import androidx.lifecycle.MediatorLiveData
import mg.business.ikonnectmobile.api.mobilemoney.MobileMoneyController

class DiscussionViewModel(application: Application) : AndroidViewModel(application) {

    // Define constants for the service providers and URIs
    companion object {
        val SERVICE_PROVIDERS = MobileMoneyController.SERVICE_PROVIDERS
        const val SMS_CONVERSATION_URI = "content://sms/conversations"
        const val SMS_URI = "content://sms"
    }

    private val _discussions = MutableLiveData<List<Discussion>>()
    val discussions: LiveData<List<Discussion>> get() = _discussions

    init {
        loadDiscussions()
    }

    private fun loadDiscussions() {
        viewModelScope.launch(Dispatchers.IO) {
            val contentResolver: ContentResolver = getApplication<Application>().contentResolver
            val projection = arrayOf(
                "thread_id", "snippet", "msg_count"
            )
            val uri = Uri.parse(SMS_CONVERSATION_URI)

            val cursor: Cursor? = try {
                contentResolver.query(uri, projection, null, null, null)
            } catch (e: SQLiteException) {
                Log.e("DiscussionViewModel", "SQL error: ${e.message}")
                null
            }

            val discussionsList = mutableListOf<Discussion>()
            Log.d("Load discussion: ", "Loading discussions...")

            cursor?.use {
                while (it.moveToNext()) {
                    val discussion = mapCursorToDiscussion(it)

                    // Filter only discussions with messages from specific service providers
                    val filteredMessages = discussion.messages.filter { message ->
                        message.senderAddress in SERVICE_PROVIDERS
                    }

                    if (filteredMessages.isNotEmpty()) {
                        discussionsList.add(discussion)
                    }
                }
            }
            discussionsList.sortByDescending { it.lastMessage?.date }
            _discussions.postValue(discussionsList)
        }
    }

    private fun logColumnFromUri(uri: Uri) {
        val contentResolver: ContentResolver = getApplication<Application>().contentResolver
        try {
            val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
            cursor?.let {
                logAvailableColumns(it, uri.toString())
                it.close()
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error querying $uri: ${e.message}")
        }
    }

    private fun logAvailableColumns(cursor: Cursor, tableName: String) {
        val columnNames = cursor.columnNames
        val columnCount = columnNames.size
        val columnNamesString = columnNames.joinToString(", ")

        Log.i("Table Info: nom de la table", "Table: $tableName")
        Log.i("Table Info: nombre de colonne", "Number of Columns: $columnCount")
        Log.i("Table Info: les colonnes ", "Columns: $columnNamesString")
    }

    private fun mapCursorToDiscussion(cursor: Cursor): Discussion {
        val threadId = cursor.safeGetInt("thread_id")
        val snippet = cursor.safeGetString("snippet") ?: ""
        val messageCount = cursor.safeGetInt("msg_count")

        val messages = getMessagesForThread(getApplication<Application>().contentResolver, threadId)
        val lastMessage = messages.lastOrNull()
        val error = when (lastMessage?.status) {
            32 -> 1
            else -> 0
        }

        return Discussion(
            threadId = threadId,
            date = lastMessage?.date ?: 0L,
            messageCount = messageCount,
            recipientIds = lastMessage?.senderAddress?.split(","),
            snippet = snippet,
            isRead = lastMessage?.isRead ?: false,
            type = lastMessage?.type ?: 0,
            error = error,
            messages = messages,
            lastMessage = lastMessage
        )
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

    private fun getMessagesForThread(contentResolver: ContentResolver, threadId: Int): List<Message> {
        val uri = Uri.parse(SMS_URI)
        val selection = "thread_id = ?"
        val selectionArgs = arrayOf(threadId.toString())
        val cursor: Cursor? = contentResolver.query(uri, null, selection, selectionArgs, "date ASC")

        val messagesList = mutableListOf<Message>()
        cursor?.use {
            while (it.moveToNext()) {
                val message = mapCursorToMessage(it)
                if (message.senderAddress in SERVICE_PROVIDERS) {
                    messagesList.add(message)
                }
            }
        }

        return messagesList
    }

    private fun mapCursorToMessage(cursor: Cursor): Message {
        val id = cursor.safeGetString("_id")
        val body = cursor.safeGetString("body")
        val date = cursor.safeGetLong("date")
        val dateSent = cursor.safeGetLong("date_sent")
        val isFromMe = cursor.safeGetString("type") == "2"

        return Message(
            id = id.toString(),
            threadId = cursor.safeGetInt("thread_id"),
            senderAddress = cursor.safeGetString("address").toString(),
            body = body.toString(),
            date = date,
            status = cursor.safeGetInt("status"),
            isRead = cursor.safeGetInt("read") == 1,
            type = cursor.safeGetInt("type"),
            dateSent = dateSent,
            isSeen = cursor.safeGetInt("seen") == 1,
            recipientIds = cursor.safeGetString("recipientIds")?.split(","),
            isFromMe = isFromMe
        )
    }

    fun getDiscussion(discussionId: String): LiveData<Discussion?> {
        val liveData = MediatorLiveData<Discussion?>()
        val observer = Observer<List<Discussion>> { discussions ->
            val selectedDiscussion = discussions.find { it.threadId.toString() == discussionId }
            liveData.value = selectedDiscussion
        }
        liveData.addSource(discussions, observer)
        return liveData
    }

    fun getMessages(discussionId: String): LiveData<List<Message>> {
        val contentResolver: ContentResolver = getApplication<Application>().contentResolver
        val uri = Uri.parse(SMS_URI)
        val selection = "thread_id = ?"
        val selectionArgs = arrayOf(discussionId)
        val cursor: Cursor? = contentResolver.query(uri, null, selection, selectionArgs, "date ASC")

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

        return MutableLiveData(messagesList)
    }
}
