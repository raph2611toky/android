package mg.business.ikonnectmobile.ui.message

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Telephony
import android.util.Log
import android.widget.Toast

class NewMessageReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null && context != null) {
            val action = intent.action
            if (action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
                val bundle = intent.extras
                if (bundle != null) {
                    val pdus = bundle.get("pdus") as Array<Any>?
                    if (pdus != null) {
                        for (pdu in pdus) {
                            val smsMessage = Telephony.Sms.Intents.getMessagesFromIntent(intent)[0]
                            val messageBody = smsMessage.messageBody
                            val sender = smsMessage.displayOriginatingAddress
                            Log.d("NewMessageReceiver", "Received message from $sender: $messageBody")

                            // Display a Toast with the message
                            Toast.makeText(context, "Message from $sender: $messageBody", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }
}
