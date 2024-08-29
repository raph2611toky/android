package mg.business.ikonnectmobile

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

import mg.business.ikonnectmobile.ui.message.MessageFragment
import mg.business.ikonnectmobile.ui.theme.IkonnectmobileTheme

class MainActivity : FragmentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            loadMessageFragment()
        } else {
            handlePermissionDenied()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IkonnectmobileTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    checkPermissionsAndLoadFragment()
                }
            }
        }
    }

    private fun checkPermissionsAndLoadFragment() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_SMS
                    ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.RECEIVE_SMS
                    ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.RECEIVE_MMS
                    ) == PackageManager.PERMISSION_GRANTED -> {
                loadMessageFragment()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE) ||
                    shouldShowRequestPermissionRationale(Manifest.permission.READ_SMS) ||
                    shouldShowRequestPermissionRationale(Manifest.permission.RECEIVE_SMS) ||
                    shouldShowRequestPermissionRationale(Manifest.permission.RECEIVE_MMS) -> {
                showPermissionExplanation()
            }
            else -> {
                // Demander les permissions nécessaires
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                requestPermissionLauncher.launch(Manifest.permission.READ_SMS)
                requestPermissionLauncher.launch(Manifest.permission.RECEIVE_SMS)
                requestPermissionLauncher.launch(Manifest.permission.RECEIVE_MMS)
            }
        }
    }


    fun loadMessageFragment() {
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace<MessageFragment>(android.R.id.content)
        }
    }

    private fun handlePermissionDenied() {
        // Afficher un Toast pour informer l'utilisateur que la permission a été refusée
        Toast.makeText(
            this,
            "Permission refusée. L'accès au stockage est nécessaire pour afficher les messages.",
            Toast.LENGTH_LONG
        ).show()

        // Ou, utiliser un Dialog pour informer l'utilisateur et fournir des options supplémentaires
        AlertDialog.Builder(this)
            .setTitle("Permission requise")
            .setMessage("L'application nécessite l'accès au stockage pour fonctionner correctement. Veuillez accorder la permission dans les paramètres.")
            .setPositiveButton("Aller aux paramètres") { _, _ ->
                // Ouvrir les paramètres de l'application pour permettre à l'utilisateur de changer la permission
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Annuler", null)
            .show()
    }


    private fun showPermissionExplanation() {
        AlertDialog.Builder(this)
            .setTitle("Pourquoi cette permission est nécessaire ?")
            .setMessage("Cette application a besoin de lire le stockage externe pour accéder aux messages stockés. Veuillez accorder cette permission pour continuer.")
            .setPositiveButton("Demander la permission") { _, _ ->
                requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            .setNegativeButton("Annuler", null)
            .show()
    }

}
