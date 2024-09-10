package mg.business.ikonnectmobile.ui.discussion

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import mg.business.ikonnectmobile.databinding.FragmentDiscussionDetailBinding
import mg.business.ikonnectmobile.ui.message.MessageAdapter

class DiscussionDetailFragment : Fragment() {

    private var _binding: FragmentDiscussionDetailBinding? = null
    private val binding get() = _binding!!

    private val discussionViewModel: DiscussionViewModel by viewModels()
    private lateinit var discussionId: String
    private lateinit var adapter: MessageAdapter
    private var isSearchBarVisible = false

    companion object {
        private const val ARG_DISCUSSION_ID = "discussion_id"

        fun newInstance(discussionId: String) = DiscussionDetailFragment().apply {
            arguments = Bundle().apply {
                putString(ARG_DISCUSSION_ID, discussionId)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            discussionId = it.getString(ARG_DISCUSSION_ID) ?: ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDiscussionDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        adapter = MessageAdapter()
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.stackFromEnd = true
        binding.messagesList.layoutManager = layoutManager
        binding.messagesList.adapter = adapter

        discussionViewModel.getMessages(discussionId).observe(viewLifecycleOwner) { messages ->
            adapter.submitList(messages) {
                binding.messagesList.scrollToPosition(adapter.itemCount - 1)
            }
        }

        binding.searchButton.setOnClickListener {
            toggleSearchBar()
        }

        binding.searchInput.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString().lowercase()
                filterMessages(query)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        discussionViewModel.getDiscussion(discussionId).observe(viewLifecycleOwner) { discussion ->
            binding.discussionHeader.text = discussion?.recipientIds?.joinToString(", ") ?: "Pas de destinataires"
        }
    }

    // Toggle the visibility of the search bar
    private fun toggleSearchBar() {
        isSearchBarVisible = !isSearchBarVisible
        if (isSearchBarVisible) {
            binding.searchInput.visibility = View.VISIBLE
            binding.discussionHeader.visibility = View.GONE
        } else {
            binding.searchInput.visibility = View.GONE
            binding.discussionHeader.visibility = View.VISIBLE
        }
    }

    // Filter messages based on search query
    private fun filterMessages(query: String) {
        discussionViewModel.getMessages(discussionId).observe(viewLifecycleOwner) { messages ->
            val filteredMessages = messages.filter {
                it.body.lowercase().contains(query)
            }
            adapter.submitList(filteredMessages)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
