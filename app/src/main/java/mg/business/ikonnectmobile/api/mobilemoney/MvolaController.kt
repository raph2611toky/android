package  mg.business.ikonnectmobile.api.mobilemoney

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteException
import android.net.Uri
import android.util.Log
import mg.business.ikonnectmobile.data.model.Discussion
import mg.business.ikonnectmobile.data.model.Message


class MvolaController(val context: Context) {

    fun loadMvolaDiscussions(): List<Discussion> {
        val contentResolver: ContentResolver = context.contentResolver
        val projection = arrayOf(
            "thread_id", "snippet", "msg_count"
        )
        val uri = Uri.parse("content://sms/conversations")

        val cursor: Cursor? = try {
            contentResolver.query(uri, projection, null, null, null)
        } catch (e: SQLiteException) {
            Log.e("MvolaController", "SQL error: ${e.message}")
            null
        }

        val discussionsList = mutableListOf<Discussion>()
        cursor?.use {
            while (it.moveToNext()) {
                val discussion = mapCursorToDiscussion(it)
                discussionsList.add(discussion)
            }
        }
        discussionsList.sortByDescending { it.lastMessage?.date }
        return discussionsList
    }

    private fun mapCursorToDiscussion(cursor: Cursor): Discussion {
        val threadId = cursor.safeGetInt("thread_id")
        val snippet = cursor.safeGetString("snippet") ?: ""
        val messageCount = cursor.safeGetInt("msg_count")

        val messages = getMessagesForThread(threadId)
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

    private fun getMessagesForThread(threadId: Int): List<Message> {
        val uri = Uri.parse("content://sms")
        val selection = "thread_id = ?"
        val selectionArgs = arrayOf(threadId.toString())
        val cursor: Cursor? = context.contentResolver.query(uri, null, selection, selectionArgs, "date ASC")

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
        val id = cursor.safeGetString("_id")
        val body = cursor.safeGetString("body")
        val date = cursor.safeGetLong("date")
        val isFromMe = cursor.safeGetString("type") == "2"
        val recipientIdsString = cursor.safeGetString("recipientIds")

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
            isSeen = cursor.safeGetInt("seen") == 1,
            recipientIds = recipientIdsString?.split(","),
            isFromMe = isFromMe
        )
    }

    fun getFilteredMvolaMessages(reference: String, numero: String, date: String): List<Message> {
        val allMessages = loadMvolaDiscussions().flatMap { it.messages }
        val ref = "Ref: "+reference
        val depot = "1/2 Depot reussi : "
        val recu = " Ar recu"
        return allMessages.filter { message ->
            message.senderAddress.contains("MVOLA", ignoreCase = true) &&
                    message.body.contains(ref) &&
                    message.body.contains(numero) &&
                    message.body.contains(date) &&
                    (message.body.contains(depot)||message.body.contains(recu))
        }
    }


}

fun Cursor.safeGetInt(columnName: String): Int {
    val index = getColumnIndex(columnName)
    return if (index != -1) getInt(index) else 0
}

fun Cursor.safeGetLong(columnName: String): Long {
    val index = getColumnIndex(columnName)
    return if (index != -1) getLong(index) else 0L
}

fun Cursor.safeGetString(columnName: String): String? {
    val index = getColumnIndex(columnName)
    return if (index != -1) getString(index) else null
}

