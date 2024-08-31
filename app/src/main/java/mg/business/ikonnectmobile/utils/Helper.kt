package mg.business.ikonnectmobile.utils

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri

fun isMessageFromMe(contentResolver: ContentResolver, messageId: String): Boolean {
    val uri = Uri.parse("content://sms/inbox")
    val projection = arrayOf("_id")
    val selection = "_id = ?"
    val selectionArgs = arrayOf(messageId)

    val cursor: Cursor? = contentResolver.query(uri, projection, selection, selectionArgs, null)

    cursor?.use {
        if (it.moveToFirst()) {
            return false
        }
    }
    return true
}
