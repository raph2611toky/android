package mg.business.ikonnectmobile.ui.message

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import mg.business.ikonnectmobile.data.model.Message
import mg.business.ikonnectmobile.databinding.FragmentMessageBinding
import java.util.*
import android.widget.Toast

class MessageFragment : Fragment() {

    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!

    private val messageViewModel: MessageViewModel by viewModels()

    private val newMessageReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "android.provider.Telephony.SMS_RECEIVED") {
                val bundle = intent.extras
                if (bundle != null) {
                    val pdus = bundle["pdus"] as Array<*>
                    for (pdu in pdus) {
                        val smsMessage = android.telephony.SmsMessage.createFromPdu(pdu as ByteArray)
                        val messageBody = smsMessage.messageBody
                        val timestamp = System.currentTimeMillis()
                        val recipientIds = listOf(smsMessage.displayOriginatingAddress ?: "")
                        val newMessage = Message(
                            id = UUID.randomUUID().toString(),
                            threadId = 0,
                            senderAddress = smsMessage.displayOriginatingAddress ?: "",
                            body = messageBody,
                            date = timestamp,
                            dateSent = timestamp,
                            isRead = false,
                            status = 0,
                            type = 1,
                            isSeen = false,
                            recipientIds = recipientIds,
                            isFromMe = false
                        )
                        messageViewModel.addMessage(newMessage)

                        Toast.makeText(context, "Message reçu: $messageBody", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMessageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = MessageAdapter()
        binding.messageList.layoutManager = LinearLayoutManager(requireContext())
        binding.messageList.adapter = adapter

        messageViewModel.messages.observe(viewLifecycleOwner) { messages ->
            adapter.submitList(messages)
        }
    }

    override fun onResume() {
        super.onResume()
        requireContext().registerReceiver(newMessageReceiver, IntentFilter("android.provider.Telephony.SMS_RECEIVED"))
    }

    override fun onPause() {
        super.onPause()
        requireContext().unregisterReceiver(newMessageReceiver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
