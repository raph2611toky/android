package mg.business.ikonnectmobile.ui.discussion

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import mg.business.ikonnectmobile.R
import mg.business.ikonnectmobile.databinding.FragmentDiscussionListBinding

class DiscussionListFragment : Fragment() {

    private var _binding: FragmentDiscussionListBinding? = null
    private val binding get() = _binding!!

    private val discussionViewModel: DiscussionViewModel by viewModels()

    private val permissionMap = mapOf(
        Manifest.permission.READ_SMS to
                "L'accès aux SMS est nécessaire pour lire vos messages.",
        Manifest.permission.RECEIVE_SMS to
                "L'accès pour recevoir des SMS est nécessaire pour vous notifier des nouveaux messages.",
        Manifest.permission.RECEIVE_MMS to
                "L'accès pour recevoir des MMS est nécessaire pour vous notifier des nouveaux messages."
    )

    private val permissionLaunchers = permissionMap.keys.associateWith { permission ->
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission accordée, charger les discussions
                loadDiscussions()
            } else {
                showPermissionExplanation(permission)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDiscussionListBinding.inflate(inflater, container, false)
        checkPermissions()
        return binding.root
    }

    private fun checkPermissions() {
        permissionMap.keys.forEach { permission ->
            when {
                ContextCompat.checkSelfPermission(requireContext(), permission) ==
                        PackageManager.PERMISSION_GRANTED -> {
                    // Permission déjà accordée
                    loadDiscussions()
                }
                shouldShowRequestPermissionRationale(permission) -> {
                    showPermissionExplanation(permission)
                }
                else -> {
                    permissionLaunchers[permission]?.launch(permission)
                }
            }
        }
    }

    private fun showPermissionExplanation(permission: String) {
        val rationale = permissionMap[permission] ?:
        "Cette permission est nécessaire pour le bon fonctionnement de l'application."

        AlertDialog.Builder(requireContext())
            .setTitle("Permission requise")
            .setMessage(rationale)
            .setPositiveButton("Demander la permission") { _, _ ->
                permissionLaunchers[permission]?.launch(permission)
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun loadDiscussions() {
        val adapter = DiscussionAdapter { discussion ->
            parentFragmentManager.beginTransaction()
                .replace(R.id.container,
                    DiscussionDetailFragment.newInstance(discussion.threadId.toString()))
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
