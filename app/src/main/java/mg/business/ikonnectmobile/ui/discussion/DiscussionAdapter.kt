package mg.business.ikonnectmobile.ui.discussion

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import mg.business.ikonnectmobile.data.model.Discussion
import mg.business.ikonnectmobile.databinding.ItemDiscussionBinding
import mg.business.ikonnectmobile.utils.DateUtils.formatDiscussionDate

class DiscussionAdapter(private val onClick: (Discussion) -> Unit) :
    ListAdapter<Discussion, DiscussionAdapter.DiscussionViewHolder>(DiscussionDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiscussionViewHolder {
        val binding = ItemDiscussionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DiscussionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DiscussionViewHolder, position: Int) {
        holder.bind(getItem(position), onClick)
    }

    class DiscussionViewHolder(private val binding: ItemDiscussionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(discussion: Discussion, onClick: (Discussion) -> Unit) {
            binding.discussionBody.text = discussion.snippet
            binding.discussionSource.text = discussion.recipientIds?.joinToString(", ")
            binding.discussionTimeElapsed.text = formatDiscussionDate(discussion.date)
            binding.root.setOnClickListener { onClick(discussion) }
        }
    }

    class DiscussionDiffCallback : DiffUtil.ItemCallback<Discussion>() {
        override fun areItemsTheSame(oldItem: Discussion, newItem: Discussion): Boolean {
            return oldItem.threadId == newItem.threadId
        }

        override fun areContentsTheSame(oldItem: Discussion, newItem: Discussion): Boolean {
            return oldItem == newItem
        }
    }
}
