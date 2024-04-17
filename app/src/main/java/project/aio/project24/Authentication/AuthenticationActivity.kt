package project.aio.project24.Authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import project.aio.project24.Main.MainActivity
import project.aio.project24.Managers.PreferenceManagers.AuthPreferenceManager
import project.aio.project24.R

class AuthenticationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)

        supportActionBar?.hide()

        val authPreferenceManager = AuthPreferenceManager(this)
        if (authPreferenceManager.getLoginStatus()) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, GoogleAuthFragment())
            transaction.commit()
        }
    }
}