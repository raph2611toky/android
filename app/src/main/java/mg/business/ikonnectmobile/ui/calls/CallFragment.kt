package mg.business.ikonnectmobile.ui.calls

import android.Manifest
import android.content.Context
import android.telecom.TelecomManager
import android.telecom.PhoneAccountHandle
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.telephony.SubscriptionManager
import androidx.activity.result.contract.ActivityResultContracts
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import mg.business.ikonnectmobile.R
import mg.business.ikonnectmobile.data.model.SimInfo
import mg.business.ikonnectmobile.ui.calls.SimFragment
import android.util.Log

class CallFragment : Fragment() {

    private lateinit var display: TextView

    private val permissionMap = mapOf(
        Manifest.permission.CALL_PHONE to
                "L'accès pour passer des appels téléphoniques est nécessaire pour effectuer des appels.",
        Manifest.permission.READ_PHONE_STATE to
                "L'accès aux informations sur l'état du téléphone est nécessaire pour gérer les appels.",
        Manifest.permission.READ_CALL_LOG to
                "L'accès aux journaux d'appels est nécessaire pour afficher les informations sur les appels passés.",
        Manifest.permission.WRITE_CALL_LOG to
                "L'accès pour modifier les journaux d'appels est nécessaire pour enregistrer les informations sur les appels.",
        Manifest.permission.PROCESS_OUTGOING_CALLS to
                "L'accès pour traiter les appels sortants est nécessaire pour détecter et gérer les appels."
    )


    private val permissionLaunchers = permissionMap.keys.associateWith { permission ->
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                showPermissionExplanation(permission)
            }
        }
    }

    private fun checkPermissions() {
        permissionMap.keys.forEach { permission ->
            when {
                ContextCompat.checkSelfPermission(requireContext(), permission) ==
                        PackageManager.PERMISSION_GRANTED -> {
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


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialpad_layout, container, false)

        display = view.findViewById(R.id.display)
        display.isSelected = true

        checkPermissions()

        val buttons = listOf(
            view.findViewById<Button>(R.id.btn_number_0),
            view.findViewById<Button>(R.id.btn_number_1),
            view.findViewById<Button>(R.id.btn_number_2),
            view.findViewById<Button>(R.id.btn_number_3),
            view.findViewById<Button>(R.id.btn_number_4),
            view.findViewById<Button>(R.id.btn_number_5),
            view.findViewById<Button>(R.id.btn_number_6),
            view.findViewById<Button>(R.id.btn_number_7),
            view.findViewById<Button>(R.id.btn_number_8),
            view.findViewById<Button>(R.id.btn_number_9),
            view.findViewById<Button>(R.id.btn_star),
            view.findViewById<Button>(R.id.btn_hash)
        )

        for (button in buttons) {
            button.setOnClickListener {
                display.append(button.text)
            }
        }

        val clearButton = view.findViewById<Button>(R.id.btn_clear)
        clearButton.setOnClickListener {
            val currentText = display.text.toString()
            if (currentText.isNotEmpty()) {
                display.text = currentText.substring(0, currentText.length - 1)
            }
        }

        val callButton = view.findViewById<Button>(R.id.btn_call)
        callButton.setOnClickListener {
            showSimSelectionDialog()
        }

        return view
    }

    private fun showSimSelectionDialog() {
        val simFragment = SimFragment()
        val simCards = simFragment.getSimCardsInfo(requireContext())
        Log.d("Call", simCards.toString())

        if (simCards.isNotEmpty()) {
            val simOptions = simCards.map {
                it.displayName + " (" + it.carrierName + ") - " + (if (it.number != "Inconnu") it.number else "Numéro non disponible")
            }

            AlertDialog.Builder(requireContext())
                .setTitle("Choisissez une carte SIM")
                .setItems(simOptions.toTypedArray()) { _, which ->
                    val selectedSim = simCards[which]
                    Log.d("call", selectedSim.toString())
                    AlertDialog.Builder(requireContext())
                        .setTitle("Détails de la carte SIM sélectionnée")
                        .setMessage(
                            """
                Carte SIM ${selectedSim.simSlotIndex} :
                Opérateur : ${selectedSim.carrierName}
                Numéro : ${selectedSim.number}
                Pays : ${selectedSim.countryIso}
                Affichage : ${selectedSim.displayName}
                ICCID : ${selectedSim.iccId}
                """.trimIndent()
                        )
                        .setPositiveButton("Appeler") { _, _ ->
                            makeCall(selectedSim.subscriptionId)
                        }
                        .setNegativeButton("Annuler", null)
                        .show()
                }
                .setNegativeButton("Annuler", null)
                .show()
        } else {
            AlertDialog.Builder(requireContext())
                .setTitle("Aucune carte SIM disponible")
                .setMessage("Veuillez insérer une carte SIM pour passer un appel.")
                .setPositiveButton("OK", null)
                .show()
        }
    }

    private fun makeCall(subscriptionId: Int) {
        Log.d("call", "Make call from subscription : $subscriptionId")
        val phoneNumber = display.text.toString()
        if (phoneNumber.isNotEmpty()) {
            val telecomManager = requireContext().getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            val subscriptionManager = requireContext().getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager

            val subscriptionInfo = subscriptionManager.getActiveSubscriptionInfo(subscriptionId)
            Log.d("call", "subscription info : $subscriptionInfo")

            val phoneAccountHandles = telecomManager.callCapablePhoneAccounts
            val phoneAccountHandle = phoneAccountHandles.find {
                Log.d("call", "phone account handle : $phoneAccountHandles")
                it.id.contains(subscriptionInfo.subscriptionId.toString())
            }

            if (phoneAccountHandle != null) {
                Log.d("call", "phone account handle found : $phoneAccountHandle")
                val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber")).apply {
                    putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandle)
                }

                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.CALL_PHONE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    startActivity(intent)
                } else {
                    requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 1)
                }
            } else {
                AlertDialog.Builder(requireContext())
                    .setTitle("Erreur")
                    .setMessage("Impossible de trouver le handle du compte téléphonique.")
                    .setPositiveButton("OK", null)
                    .show()
            }
        } else {
            AlertDialog.Builder(requireContext())
                .setTitle("Numéro manquant")
                .setMessage("Veuillez entrer un numéro de téléphone.")
                .setPositiveButton("OK", null)
                .show()
        }
    }

}
