package mg.business.ikonnectmobile.ui.calls

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import mg.business.ikonnectmobile.R

class CallFragment : Fragment() {

    private lateinit var display: TextView

    private val permissionMap = mapOf(
        Manifest.permission.CALL_PHONE to
                "L'accès pour passer des appels téléphoniques est nécessaire pour effectuer des appels.",
        Manifest.permission.READ_PHONE_STATE to
                "L'accès aux informations sur l'état du téléphone est nécessaire pour gérer les appels."
    )

    private val permissionLaunchers = permissionMap.keys.associateWith { permission ->
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                showPermissionExplanation(permission)
            }
        }
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
            makeCall()
        }

        return view
    }

    private fun checkPermissions() {
        permissionMap.keys.forEach { permission ->
            when {
                ContextCompat.checkSelfPermission(requireContext(), permission) ==
                        PackageManager.PERMISSION_GRANTED -> {
                    // Permission accordée, rien à faire
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

    private fun makeCall() {
        val phoneNumber = display.text.toString()
        if (phoneNumber.isNotEmpty()) {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                startActivity(intent)
            } else {
                permissionLaunchers[Manifest.permission.CALL_PHONE]?.launch(
                    Manifest.permission.CALL_PHONE
                )
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
