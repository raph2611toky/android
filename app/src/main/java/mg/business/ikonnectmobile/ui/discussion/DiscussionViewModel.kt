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
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import mg.business.ikonnectmobile.data.model.Discussion
import mg.business.ikonnectmobile.data.model.Message

class DiscussionViewModel(application: Application) : AndroidViewModel(application) {

    private val _discussions = MutableLiveData<List<Discussion>>()
    val discussions: LiveData<List<Discussion>> get() = _discussions

    init {
        loadDiscussions()
    }

    private fun loadDiscussions() {
        viewModelScope.launch(Dispatchers.IO) {
            val contentResolver: ContentResolver = getApplication<Application>().contentResolver
            val projection = arrayOf(
                "thread_id","snippet", "msg_count"
            )
            val uri = Uri.parse("content://sms/conversations")

            val cursor: Cursor? = try {
                contentResolver.query(uri, projection, null, null, null)
            } catch (e: SQLiteException) {
                Log.e("DiscussionViewModel", "SQL error: ${e.message}")
                null
            }

            val discussionsList = mutableListOf<Discussion>()

            cursor?.use {
                while (it.moveToNext()) {
                    val discussion = mapCursorToDiscussion(it)
                    discussionsList.add(discussion)
                }
            }

            _discussions.postValue(discussionsList)
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
        logAvailableColumns(cursor, "discussion")
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
            recipientIds = lastMessage?.senderAddress?.split(" "),
            snippet = snippet,
            isRead = lastMessage?.isRead ?: false,
            type = lastMessage?.type ?: 0,
            error = error,
            messages = messages
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
        val uri = Uri.parse("content://sms")
        val selection = "thread_id = ?"
        val selectionArgs = arrayOf(threadId.toString())
        val cursor: Cursor? = contentResolver.query(uri, null, selection, selectionArgs, "date ASC")

        val messagesList = mutableListOf<Message>()
        cursor?.use {
            while (it.moveToNext()) {
                val message = mapCursorToMessage(it)
                messagesList.add(message)
            }
        }

        return messagesList
    }

    private fun mapCursorToMessage(cursor: Cursor): Message {
        logAvailableColumns(cursor, "message")
        val id = cursor.safeGetString("_id")
        val body = cursor.safeGetString("body")
        val date = cursor.safeGetLong("date")

        return Message(
            id = id.toString(),
            threadId = cursor.safeGetInt("thread_id"),
            senderAddress = cursor.safeGetString("address").toString(),
            body = body.toString(),
            date = date,
            status = cursor.safeGetInt("status"),
            isRead = cursor.safeGetInt("read") == 1,
            type = cursor.safeGetInt("type"),
            dateSent = cursor.safeGetLong("date_sent"),
            isSeen = cursor.safeGetInt("seen") == 1
        )
    }



    fun getDiscussion(discussionId: String): LiveData<Discussion?> {
        val selectedDiscussion = _discussions.value?.find { it.threadId.toString() == discussionId }
        val liveData = MutableLiveData<Discussion?>()
        liveData.value = selectedDiscussion
        return liveData
    }

    fun getMessages(discussionId: String): LiveData<List<Message>> {
        val contentResolver: ContentResolver = getApplication<Application>().contentResolver
        val uri = Uri.parse("content://sms")
        val selection = "thread_id = ?"
        val selectionArgs = arrayOf(discussionId)
        val cursor: Cursor? = contentResolver.query(uri, null, selection, selectionArgs, "date ASC")

        val messagesList = mutableListOf<Message>()
        cursor?.use {
            val idIndex = it.getColumnIndex("_id")
            val bodyIndex = it.getColumnIndex("body")
            val dateIndex = it.getColumnIndex("date")

            while (it.moveToNext()) {
                val id = if (idIndex >= 0) it.getString(idIndex) else ""
                val body = if (bodyIndex >= 0) it.getString(bodyIndex) else ""
                val date = if (dateIndex >= 0) it.getLong(dateIndex) else 0L

                messagesList.add(
                    Message(
                        id = id,
                        threadId = discussionId.toInt(),
                        senderAddress = "",
                        body = body,
                        date = date,
                        status = 0,
                        isRead = false,
                        type = 0,
                        dateSent = 2131232,
                        isSeen = false
                    )
                )
            }
        }

        return MutableLiveData(messagesList)
    }

}
