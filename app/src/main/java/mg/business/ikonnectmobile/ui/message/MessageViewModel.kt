package mg.business.ikonnectmobile.ui.message

import android.app.Application
import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import mg.business.ikonnectmobile.data.model.Message
import java.text.SimpleDateFormat
import java.util.*

class MessageViewModel(application: Application) : AndroidViewModel(application) {

    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> get() = _messages

    init {
        loadMessages()
    }

    private fun loadMessages() {
        val contentResolver: ContentResolver = getApplication<Application>().contentResolver
        val uri = Uri.parse("content://sms/inbox")
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)

        val messagesList = mutableListOf<Message>()

        cursor?.let {
            val idIndex = it.getColumnIndex("_id")
            val bodyIndex = it.getColumnIndex("body")
            val dateIndex = it.getColumnIndex("date")

            while (it.moveToNext()) {
                val id = it.getString(idIndex)
                val body = it.getString(bodyIndex)
                val date = it.getLong(dateIndex)
                val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date(date))
                messagesList.add(Message(id, "SMS", body, timestamp))
            }
            it.close()
        }

        _messages.value = messagesList
    }

    fun addMessage(message: Message) {
        val currentMessages = _messages.value?.toMutableList() ?: mutableListOf()
        currentMessages.add(0, message)
        _messages.value = currentMessages
    }
}
