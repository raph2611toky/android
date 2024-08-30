package mg.business.ikonnectmobile.ui.discussion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import mg.business.ikonnectmobile.R
import mg.business.ikonnectmobile.databinding.FragmentDiscussionListBinding

class DiscussionListFragment : Fragment() {

    private var _binding: FragmentDiscussionListBinding? = null
    private val binding get() = _binding!!

    private val discussionViewModel: DiscussionViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDiscussionListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = DiscussionAdapter { discussion ->
            parentFragmentManager.beginTransaction()
                .replace(R.id.container, DiscussionDetailFragment.newInstance(discussion.threadId.toString()))
                .addToBackStack(null)
                .commit()
        }
        binding.discussionList.adapter = adapter
        binding.discussionList.layoutManager = LinearLayoutManager(requireContext())

        discussionViewModel.discussions.observe(viewLifecycleOwner) { discussions ->
            adapter.submitList(discussions)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
