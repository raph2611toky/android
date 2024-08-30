package mg.business.ikonnectmobile

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import mg.business.ikonnectmobile.ui.discussion.DiscussionListFragment

class MainActivity : FragmentActivity() {

    private val permissionMap = mapOf(
        Manifest.permission.READ_EXTERNAL_STORAGE to "L'accès au stockage est nécessaire pour afficher les messages.",
        Manifest.permission.READ_SMS to "L'accès aux SMS est nécessaire pour lire vos messages.",
        Manifest.permission.RECEIVE_SMS to "L'accès pour recevoir des SMS est nécessaire pour vous notifier des nouveaux messages.",
        Manifest.permission.RECEIVE_MMS to "L'accès pour recevoir des MMS est nécessaire pour vous notifier des nouveaux messages."
    )

    private val permissionLaunchers = permissionMap.keys.associateWith { permission ->
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                loadDiscussionListFragment()
            } else {
                handlePermissionDenied(permission)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        checkPermissionsAndLoadFragment()
    }

    private fun checkPermissionsAndLoadFragment() {
        permissionMap.keys.forEach { permission ->
            when {
                ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                    loadDiscussionListFragment()
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

    private fun handlePermissionDenied(permission: String) {
        val rationale = permissionMap[permission] ?: "Cette permission est nécessaire pour le bon fonctionnement de l'application."

        Toast.makeText(
            this,
            "Permission refusée. $rationale",
            Toast.LENGTH_LONG
        ).show()

        AlertDialog.Builder(this)
            .setTitle("Permission requise")
            .setMessage(rationale + " Veuillez accorder la permission dans les paramètres.")
            .setPositiveButton("Paramètres") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun showPermissionExplanation(permission: String) {
        val rationale = permissionMap[permission] ?: "Cette permission est nécessaire pour le bon fonctionnement de l'application."

        AlertDialog.Builder(this)
            .setTitle("Pourquoi cette permission est nécessaire ?")
            .setMessage(rationale)
            .setPositiveButton("Demander la permission") { _, _ ->
                permissionLaunchers[permission]?.launch(permission)
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

    private fun loadDiscussionListFragment() {
        loadFragment(DiscussionListFragment())
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.container, fragment)
        }
    }
}
