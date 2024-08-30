package mg.business.ikonnectmobile.ui.discussion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import mg.business.ikonnectmobile.ui.message.MessageAdapter
import mg.business.ikonnectmobile.databinding.FragmentDiscussionDetailBinding

class DiscussionDetailFragment : Fragment() {

    private var _binding: FragmentDiscussionDetailBinding? = null
    private val binding get() = _binding!!

    private val discussionViewModel: DiscussionViewModel by viewModels()
    private lateinit var discussionId: String

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

        val adapter = MessageAdapter()
        binding.messagesList.adapter = adapter
        binding.messagesList.layoutManager = LinearLayoutManager(requireContext())

        discussionViewModel.getMessages(discussionId).observe(viewLifecycleOwner) { messages ->
            adapter.submitList(messages)
        }

        discussionViewModel.getDiscussion(discussionId).observe(viewLifecycleOwner) { discussion ->
            binding.discussionHeader.text = discussion?.snippet  // Utilisez l'aperçu de la discussion
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
