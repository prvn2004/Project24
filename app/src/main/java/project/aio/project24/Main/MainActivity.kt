package project.aio.project24.Main

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.messaging.FirebaseMessaging
import project.aio.project24.R
import project.aio.project24.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        FirebaseMessaging.getInstance().subscribeToTopic("notification")

//        FirebaseAnalytics.getInstance(this);
//        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);

//        val transaction = supportFragmentManager.beginTransaction()
//        transaction.replace(R.id.mainLayout, ChatFragment())
//        transaction.addToBackStack(null)
//        transaction.commit()
    }

}