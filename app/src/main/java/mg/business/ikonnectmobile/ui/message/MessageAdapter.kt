// MessageAdapter.kt
package mg.business.ikonnectmobile.ui.message

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import mg.business.ikonnectmobile.databinding.ItemMessageBinding
import mg.business.ikonnectmobile.data.model.Message
import mg.business.ikonnectmobile.utils.DateUtils.formatDate
import androidx.constraintlayout.widget.ConstraintLayout
import mg.business.ikonnectmobile.R
import android.content.Context

class MessageAdapter :
    ListAdapter<Message, MessageAdapter.MessageViewHolder>(MessageDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class MessageViewHolder(private val binding: ItemMessageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: Message) {
            binding.messageContent.text = message.body
            binding.messageTimestamp.text = formatDate(message.date)

            val layoutParams = binding.messageContent.layoutParams as ConstraintLayout.LayoutParams
            val timestampParams = binding.messageTimestamp.layoutParams as ConstraintLayout.LayoutParams

            if (message.isFromMe) {
                layoutParams.horizontalBias = 1.0f
                timestampParams.horizontalBias = 1.0f

                layoutParams.marginStart = 64.dpToPx(binding.root.context)
                layoutParams.marginEnd = 16.dpToPx(binding.root.context)
                timestampParams.marginStart = layoutParams.marginStart
                timestampParams.marginEnd = layoutParams.marginEnd

                binding.messageContent.setBackgroundResource(R.drawable.message_background_sent)
                binding.messageContent.setTextColor(binding.root.context.getColor(R.color.white))
            } else {
                layoutParams.horizontalBias = 0.0f
                timestampParams.horizontalBias = 0.0f

                layoutParams.marginStart = 16.dpToPx(binding.root.context)
                layoutParams.marginEnd = 64.dpToPx(binding.root.context)
                timestampParams.marginStart = layoutParams.marginStart
                timestampParams.marginEnd = layoutParams.marginEnd

                binding.messageContent.setBackgroundResource(R.drawable.message_background_received)
                binding.messageContent.setTextColor(binding.root.context.getColor(R.color.black))
            }

            binding.messageContent.layoutParams = layoutParams
            binding.messageTimestamp.layoutParams = timestampParams
        }

        private fun Int.dpToPx(context: Context): Int {
            return (this * context.resources.displayMetrics.density).toInt()
        }
    }



    class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    }
}
