package project.aio.project24.Api.Sockets

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import project.aio.project24.Main.MainActivity
import project.aio.project24.Managers.Functions.PreferenceDatabaseManager
import project.aio.project24.Managers.PreferenceManagers.NotificationPreferenceManager
import project.aio.project24.R


class MySocketService : FirebaseMessagingService() {
    private lateinit var preferenceDatabaseManager: PreferenceDatabaseManager
    private lateinit var notificationPreferenceManager: NotificationPreferenceManager

    override fun onNewToken(token: String) {
        Log.d("testing", "Refreshed token for FCM: $token")
        preferenceDatabaseManager = PreferenceDatabaseManager(applicationContext)
        notificationPreferenceManager = NotificationPreferenceManager(applicationContext)

        GlobalScope.launch { sendRegistrationToServer(token) }
    }

    private suspend fun sendRegistrationToServer(token: String) {
        notificationPreferenceManager.setFcmTokenValue(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {1
        // Get the order_id from the data payload

        val title = remoteMessage.notification?.title
        val message = remoteMessage.notification?.body
//        val play_check = remoteMessage.data["play_check"].toString()

//        Log.d("123", play_check.toString())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "MyChannelName"
            val descriptionText = "My channel description"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("1", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = PendingIntent.getActivity(
                this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val notificationBuilder = NotificationCompat.Builder(this, "1")
                .setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.model_image)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.notify(1, notificationBuilder.build())
        }


//    }
}