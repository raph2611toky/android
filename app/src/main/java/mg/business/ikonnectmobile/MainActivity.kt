package mg.business.ikonnectmobile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import android.net.Uri
import android.provider.Settings
import android.app.ActivityManager
import mg.business.ikonnectmobile.ui.calls.CallFragment
import mg.business.ikonnectmobile.ui.discussion.DiscussionListFragment
import mg.business.ikonnectmobile.api.ApiServerService

class MainActivity : AppCompatActivity() {

    private var permissionsGranted = mutableSetOf<String>()
    private var permissionsDenied = mutableSetOf<String>()

    companion object {
        const val REQUEST_CODE_FOREGROUND_SERVICE = 1001
    }

    private val permissionMap = mapOf(
        Manifest.permission.READ_EXTERNAL_STORAGE to
                "L'accès au stockage est nécessaire pour afficher les messages."
    )

    private val permissionLaunchers = permissionMap.keys.associateWith { permission ->
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                permissionsGranted.add(permission)
                checkAllPermissionsGranted()
            } else {
                permissionsDenied.add(permission)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupBottomNavigation()

        checkPermissionsAndLoadFragment()
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView = findViewById<BottomNavigationView>(
            R.id.bottom_navigation
        )
        bottomNavigationView.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_messages -> {
                    loadFragment(DiscussionListFragment())
                    true
                }
                R.id.nav_calls -> {
                    loadFragment(CallFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun checkPermissionsAndLoadFragment() {
        permissionMap.keys.forEach { permission ->
            when {
                ContextCompat.checkSelfPermission(this, permission) ==
                        PackageManager.PERMISSION_GRANTED -> {
                    permissionsGranted.add(permission)
                }
                else -> {
                    permissionLaunchers[permission]?.launch(permission)
                }
            }
        }
        // Check if all permissions are granted
        checkAllPermissionsGranted()
    }

    private fun checkAllPermissionsGranted() {
        if (permissionMap.keys.all { permissionsGranted.contains(it) }) {
            loadDiscussionListFragment()
            startApiServerService()
        } else if (permissionsDenied.isNotEmpty()) {
            showPermissionDeniedLayout()
        }
    }

    private fun showPermissionDeniedLayout() {
        setContentView(R.layout.permission_denied_layout)

        findViewById<Button>(R.id.goToSettingsButton).setOnClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri: Uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivity(intent)
        }
    }

    private fun requestForegroundServicePermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED) {

            // Demander la permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.FOREGROUND_SERVICE),
                REQUEST_CODE_FOREGROUND_SERVICE
            )
        } else {
            Log.d("ForegroundService", "Permission déjà accordée.")
        }
    }

    private fun handlePermissionDenied(permission: String) {
        val rationale = permissionMap[permission] ?:
        "Cette permission est nécessaire pour le bon fonctionnement de l'application."

        AlertDialog.Builder(this)
            .setTitle("Permission requise")
            .setMessage("$rationale Veuillez accorder la permission dans les paramètres.")
            .setPositiveButton("Paramètres") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                val uri: Uri = Uri.fromParts("package", packageName, null)
                intent.data = uri
                startActivity(intent)
            }
            .setNegativeButton("Annuler", null)
            .setOnDismissListener {
                checkAllPermissionsGranted()
            }
            .show()
    }

    private fun showPermissionExplanation(permission: String) {
        val rationale = permissionMap[permission] ?:
        "Cette permission est nécessaire pour le bon fonctionnement de l'application."

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

    private fun isServiceRunning(serviceClass: Class<*>): Boolean {
        val manager = getSystemService(ActivityManager::class.java)
        for (service in manager.getRunningServices(Int.MAX_VALUE)) {
            if (serviceClass.name == service.service.className) {
                return true
            }
        }
        return false
    }

    private fun startApiServerService() {
        if (!isServiceRunning(ApiServerService::class.java)) {
            val intent = Intent(this, ApiServerService::class.java)
            ContextCompat.startForegroundService(this, intent)
            Log.i("ServiceStatus", "ApiServerService démarré avec succès.")
        } else {
            Log.i("ServiceStatus", "ApiServerService est déjà en cours d'exécution.")
        }
    }
}
