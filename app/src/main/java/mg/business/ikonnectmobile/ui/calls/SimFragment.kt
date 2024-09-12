package mg.business.ikonnectmobile.ui.calls

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import mg.business.ikonnectmobile.data.model.SimInfo

class SimFragment : Fragment() {

    private val permissionMap = mapOf(
        Manifest.permission.READ_PHONE_STATE to
                "L'accès à l'état du téléphone est nécessaire pour récupérer les informations SIM.",
        Manifest.permission.READ_PHONE_NUMBERS to
                "L'accès aux numéros de téléphone est nécessaire pour identifier les cartes SIM."
    )


    private val permissionLaunchers = permissionMap.keys.associateWith { permission ->
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                context?.let { getSimCardsInfo(it) }
            } else {
                showPermissionExplanation(permission)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("Sim", "View is Created...")

        view.post {
            context?.let { context ->
                if (checkPermissions(context)) {
                    getSimCardsInfo(context)
                } else {
                    requestRequiredPermissions()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        permissionMap.keys.forEach { permission ->
            if (ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                permissionLaunchers[permission]?.launch(permission)
            }
        }
    }

    private fun requestRequiredPermissions() {
        permissionMap.keys.forEach { permission ->
            if (permission == Manifest.permission.READ_PHONE_NUMBERS && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                permissionLaunchers[permission]?.launch(permission)
            } else if (permission != Manifest.permission.READ_PHONE_NUMBERS) {
                permissionLaunchers[permission]?.launch(permission)
            }
        }
    }

    fun getSimCardsInfo(context: Context): List<SimInfo> {
        val simInfoList = mutableListOf<SimInfo>()

        if (!checkPermissions(context)) {
            return simInfoList
        }
        Log.d("Call", "all permission is checked...")

        try {
            val subscriptionManager = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                val subscriptionInfoList = subscriptionManager.activeSubscriptionInfoList ?: emptyList<SubscriptionInfo>()

                subscriptionInfoList.forEach { info ->
                    Log.d("call", info.toString())
                    val simInfo = SimInfo(
                        simSlotIndex = info.simSlotIndex,
                        carrierName = info.carrierName?.toString() ?: "Inconnu",
                        countryIso = info.countryIso ?: "Inconnu",
                        displayName = info.displayName?.toString() ?: "Inconnu",
                        iccId = info.iccId ?: "Inconnu",
                        number = info.number ?: "Inconnu",
                        subscriptionId = info.subscriptionId,
                        isActive = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            info.isEmbedded
                        } else {
                            false
                        }
                    )
                    simInfoList.add(simInfo)
                }
            }
        } catch (e: SecurityException) {
            Log.e("SimUtils", "Erreur de permissions lors de la récupération des informations SIM: ${e.message}")
        } catch (e: Exception) {
            Log.e("SimUtils", "Erreur lors de la récupération des informations SIM: ${e.message}")
        }

        return simInfoList
    }

    private fun checkPermissions(context: Context): Boolean {
        var allPermissionsGranted = true
        permissionMap.keys.forEach { permission ->
            Log.d("call", permission)
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false
                Log.d("call", "Permission not Granted..")
            }
            Log.d("call", "Permission Granted..")
        }
        return allPermissionsGranted
    }

    private fun showPermissionExplanation(permission: String) {
        val rationale = permissionMap[permission]
            ?: "Cette permission est nécessaire pour le bon fonctionnement de l'application."

        AlertDialog.Builder(requireContext())
            .setTitle("Permission requise")
            .setMessage(rationale)
            .setPositiveButton("Demander la permission") { _, _ ->
                permissionLaunchers[permission]?.launch(permission)
            }
            .setNegativeButton("Annuler", null)
            .show()
    }
}
