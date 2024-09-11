package  mg.business.ikonnectmobile.api.mobilemoney

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteException
import android.net.Uri
import android.util.Log
import mg.business.ikonnectmobile.data.model.Discussion
import mg.business.ikonnectmobile.data.model.Message
import mg.business.ikonnectmobile.utils.DateUtils.matchDate


class MobileMoneyController(val context: Context) {

    fun loadDiscussions(): List<Discussion> {
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
        val cursor: Cursor? =
            context.contentResolver.query(uri, null, selection, selectionArgs, "date ASC")

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
    fun getFilteredMessages(reference: String, numero: String, date: String, messageDao: MessageDao): List<Message> {
        messageDao.open()
        val allSavedMessages = messageDao.getAllMessages()

        val messageAlreadyExists = allSavedMessages.any { message ->
            message.senderAddress.contains(SERVICE_PROVIDERS[MVOLA_PROVIDER_INDEX], ignoreCase = true) &&
                    message.body.contains(CHAINE_REFERENCE + reference) &&
                    message.body.contains(numero) &&
                    matchDate(message.body, date, message.date) &&
                    (message.body.contains(CHAINE_DEPOT_FROM_TELMA_TO_TELMA) || message.body.contains(CHAINE_RECU_FROM_TELMA_TO_TELMA)) ||
                    message.senderAddress.contains(SERVICE_PROVIDERS[ORANGEMONEY_PROVIDER_INDEX], ignoreCase = true) &&
                    Regex("(?i)" + CHAINE_TRANS_ID.replace("Id", "[Ii]d").replace("Trans", "[Tt]rans").replace(":", "\\s*:\\s*") + reference.replace(" ", "\\s*"))
                        .containsMatchIn(message.body) &&
                    matchDate(message.body, date, message.date) &&
                    (Regex("(?i)" + CHAINE_DEPOT_FROM_ORANGE_TO_ORANGE.replace("<chiffre>", "\\d+").replace("<numero_chiffre>", numero.replace(" ", "\\s*")))
                        .containsMatchIn(message.body) ||
                            Regex("(?i)" + CHAINE_TRANSFERT_RECU).containsMatchIn(message.body))
        }

        if (messageAlreadyExists) {
            messageDao.close()
            throw IllegalArgumentException("Ce message a déjà été utilisé auparavant.")
        }

        val mvolaMessages = getFilteredMvolaMessages(reference, numero, date)
        if (mvolaMessages.isNotEmpty()) {
            messageDao.close()
            return mvolaMessages
        }

        val orangeMoneyMessages = getFilteredOrangeMoneyMessages(reference, numero, date)
        messageDao.close()
        return orangeMoneyMessages
    }


    fun getFilteredMvolaMessages(reference: String, numero: String, date: String): List<Message> {
        val allMessages = loadDiscussions().flatMap { it.messages }
        val ref = CHAINE_REFERENCE + reference
        val depot = CHAINE_DEPOT_FROM_TELMA_TO_TELMA
        val recu = CHAINE_RECU_FROM_TELMA_TO_TELMA
        return allMessages.filter { message ->
            message.senderAddress.contains(SERVICE_PROVIDERS[MVOLA_PROVIDER_INDEX]) &&
                    message.body.contains(ref) &&
                    message.body.contains(numero) &&
                    matchDate(message.body, date, message.date) &&
                    (message.body.contains(depot) || message.body.contains(recu))
        }
    }

    fun getFilteredOrangeMoneyMessages(reference: String, numero: String, date: String): List<Message> {
        val allMessages = loadDiscussions().flatMap { it.messages }

        val refRegex = Regex("(?i)" + CHAINE_TRANS_ID
            .replace("Id", "[Ii]d")
            .replace("Trans", "[Tt]rans")
            .replace(":", "\\s*:\\s*")
                + reference.replace(" ", "\\s*"))

        val regexDepot = Regex("(?i)" + CHAINE_DEPOT_FROM_ORANGE_TO_ORANGE
            .replace("<chiffre>", "\\d+")
            .replace("<numero_chiffre>", numero.replace(" ", "\\s*")))

        val regexTransfertRecu = Regex("(?i)" + CHAINE_TRANSFERT_RECU)
        Log.d("Routes", "Find in messages from orange money...")
        return allMessages.filter { message ->
            if(message.senderAddress.contains(SERVICE_PROVIDERS[ORANGEMONEY_PROVIDER_INDEX])){
                Log.d("Routes", message.body)
                Log.d("Routes", refRegex.containsMatchIn((message.body)).toString())
                Log.d("Routes", matchDate(message.body, date, message.date).toString())
                Log.d("Routes", regexDepot.containsMatchIn(message.body).toString())
                Log.d("Routes", regexTransfertRecu.containsMatchIn(message.body).toString())
            }
            message.senderAddress.contains(SERVICE_PROVIDERS[ORANGEMONEY_PROVIDER_INDEX]) &&
                    refRegex.containsMatchIn(message.body) &&
                    matchDate(message.body, date, message.date) &&
                    (regexDepot.containsMatchIn(message.body) || regexTransfertRecu.containsMatchIn(message.body))
        }
    }


    companion object {
        val SERVICE_PROVIDERS = listOf("MVola", "OrangeMoney", "AirtelMoney")
        const val MVOLA_PROVIDER_INDEX = 0
        const val ORANGEMONEY_PROVIDER_INDEX = 1
        const val AIRTELMONEY_PROVIDER_INDEX = 2
        const val CHAINE_REFERENCE = "Ref: "
        const val CHAINE_DEPOT_FROM_TELMA_TO_TELMA = "1/2 Depot reussi : "
        const val CHAINE_RECU_FROM_TELMA_TO_TELMA = " Ar recu"

        const val CHAINE_TRANS_ID = "Trans\\s*Id\\s*:\\s*"
        const val CHAINE_DEPOT_FROM_ORANGE_TO_ORANGE = "Votre\\s*dépot\\s*de\\s*<chiffre>\\s*Ar\\s*par\\s*le\\s*<numero_chiffre>\\s*est\\s*réussi\\s*."
        const val CHAINE_TRANSFERT_RECU = "Vous\\s*avez\\s*[rR][éeèe]{1}[çc]?[uU]\\s*un\\s*transfert\\s*de"
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

