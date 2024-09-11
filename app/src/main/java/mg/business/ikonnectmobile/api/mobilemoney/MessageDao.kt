package mg.business.ikonnectmobile.api.mobilemoney

import mg.business.ikonnectmobile.data.DatabaseHelper
import mg.business.ikonnectmobile.data.model.Message
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.Cursor

class MessageDao(private val dbHelper: DatabaseHelper) {
    private var database: SQLiteDatabase? = null

    fun open() {
        database = dbHelper.writableDatabase
    }

    fun close() {
        database?.close()
    }

    fun saveMessage(message: Message): Long {
        val contentValues = ContentValues().apply {
            put(DatabaseHelper.COLUMN_MESSAGE_ID, message.id)
            put(DatabaseHelper.COLUMN_THREAD_ID, message.threadId)
            put(DatabaseHelper.COLUMN_SENDER_ADDRESS, message.senderAddress)
            put(DatabaseHelper.COLUMN_BODY, message.body)
            put(DatabaseHelper.COLUMN_DATE, message.date)
            put(DatabaseHelper.COLUMN_DATE_SENT, message.dateSent)
            put(DatabaseHelper.COLUMN_IS_READ, if (message.isRead) 1 else 0)
            put(DatabaseHelper.COLUMN_STATUS, message.status)
            put(DatabaseHelper.COLUMN_TYPE, message.type)
            put(DatabaseHelper.COLUMN_IS_SEEN, if (message.isSeen) 1 else 0)
            put(DatabaseHelper.COLUMN_IS_FROM_ME, if (message.isFromMe) 1 else 0)
        }
        return database?.insert(DatabaseHelper.TABLE_MESSAGE, null, contentValues) ?: -1
    }

    fun getMessageById(id: String): Message? {
        val cursor = database?.query(
            DatabaseHelper.TABLE_MESSAGE,
            null,
            "id = ?",
            arrayOf(id),
            null,
            null,
            null
        )
        cursor?.use {
            if (it.moveToFirst()) {
                return mapCursorToMessage(it)
            }
        }
        return null
    }

    fun getAllMessages(): List<Message> {
        val cursor: Cursor? = database?.query(DatabaseHelper.TABLE_MESSAGE, null, null, null, null, null, null)
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
        return Message(
            id = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_MESSAGE_ID)) ?: "",
            threadId = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_THREAD_ID)),
            senderAddress = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_SENDER_ADDRESS)) ?: "",
            body = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_BODY)) ?: "",
            date = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE)),
            dateSent = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE_SENT)),
            isRead = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IS_READ)) == 1,
            status = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_STATUS)),
            type = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TYPE)),
            isSeen = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IS_SEEN)) == 1,
            recipientIds = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_RECIPIENT_IDS))?.split(",") ?: emptyList(),
            isFromMe = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IS_FROM_ME)) == 1
        )
    }

}
