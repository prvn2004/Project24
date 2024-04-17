package project.aio.project24.Managers.Functions.PermissionManagers

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.view.LayoutInflater
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.core.content.PermissionChecker.checkSelfPermission
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import project.aio.project24.Managers.Functions.PreferenceDatabaseManager
import project.aio.project24.Managers.PreferenceManagers.NotificationPreferenceManager
import project.aio.project24.R

class NotificationPermissionManagers(val context: Activity, val fragment: Fragment) {

    val preferenceDatabaseManager = PreferenceDatabaseManager(context)
    val notificationPreferenceManager = NotificationPreferenceManager(context)
    val dialog : AlertDialog = AlertDialog.Builder(context).create()

    val launcher = fragment.registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            dialog.dismiss()
            notificationPreferenceManager.setNotificationEnabled(true)
        } else {
            dialog.dismiss()
            // permission denied or forever denied
        }
    }

    fun checkPermissions() {
        if (checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PermissionChecker.PERMISSION_GRANTED
        ) {
            // permission granted
        } else {
            showDialogWithCustomLayout(context)

//            if (shouldShowRequestPermissionRationale(
//                    context,
//                    Manifest.permission.POST_NOTIFICATIONS
//                )
//            ) {
//            } else {
////             first request or forever denied case
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                    launcher.launch(Manifest.permission.POST_NOTIFICATIONS)

            CoroutineScope(Dispatchers.IO).launch {
                preferenceDatabaseManager.addPreference(
                    notificationPreferenceManager.NotificationPreferences,
                    notificationPreferenceManager.IS_ENABLED,
                    notificationPreferenceManager.isNotificationEnabled().toString()
                )
                preferenceDatabaseManager.addPreference(
                    notificationPreferenceManager.NotificationPreferences,
                    notificationPreferenceManager.FCM_TOKEN_VALUE,
                    notificationPreferenceManager.getFcmTokenValue().toString()
                )
            }
//                }
//            }
//        }
        }
    }

        fun showDialogWithCustomLayout(context: Context) {
            val dialogView =
                LayoutInflater.from(context).inflate(R.layout.dialog_notification_manager, null)

//            val dialog = AlertDialog.Builder(context)
//                .setView(dialogView)
//                .create()

            dialog.setView(dialogView)

            val button1 = dialogView.findViewById<Button>(R.id.btn_allow)
            val button2 = dialogView.findViewById<Button>(R.id.btn_not_allow)

            button1.setOnClickListener {
                launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }

            button2.setOnClickListener {
                // Implement your logic here for Button 2
                dialog.dismiss()
            }

            dialog.show()
        }

}