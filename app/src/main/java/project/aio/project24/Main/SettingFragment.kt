package project.aio.project24.Main

import UserGoogleDetailsManager
import android.content.Intent
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GravityCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Scope
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import project.aio.project24.Api.GmailApi.GmailAuthCodeApiService
import project.aio.project24.Managers.Functions.CommonFunctionManager
import project.aio.project24.Managers.Functions.PreferenceDatabaseManager
import project.aio.project24.Managers.PreferenceManagers.GmailAuthorisationPreferenceManager
import project.aio.project24.R
import project.aio.project24.databinding.FragmentSettingBinding

class SettingFragment : Fragment(), GoogleApiClient.OnConnectionFailedListener {

    companion object {
        private val RC_SIGN_IN = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    private lateinit var binding: FragmentSettingBinding

    private lateinit var gmailAuthorisationPreferenceManager: GmailAuthorisationPreferenceManager

    private lateinit var googleApiClient: GoogleApiClient

    private val commonFunctionManager = CommonFunctionManager()

    private lateinit var userGoogleDetailsManager: UserGoogleDetailsManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        val view = binding.root

        gmailAuthorisationPreferenceManager = GmailAuthorisationPreferenceManager(requireContext())
        userGoogleDetailsManager = UserGoogleDetailsManager(requireContext())

        setupData()

        binding.backButton.setOnClickListener {
            findNavController().navigate(R.id.action_addNoteFragment_to_ChatFragment)
        }

//        val callback = object : OnBackPressedCallback(true) {
//            override fun handleOnBackPressed() {
//                findNavController().navigateUp()
//            }
//        }
//
//        // Add the callback to the onBackPressedDispatcher
//        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        return view
    }

    fun setupData() {
        val grant_access_button = binding.btnGrantAccess
        val expand_more_email = binding.expandMoreEmail
        val base_view = binding.cardView

        val hiddenView = binding.hiddenViewEmail

        expand_more_email.setOnClickListener {
            if (hiddenView.getVisibility() === View.VISIBLE) {
                TransitionManager.beginDelayedTransition(base_view, AutoTransition())
                hiddenView.setVisibility(View.GONE)
                expand_more_email.setImageResource(R.drawable.ic_expand_less_black)
            } else {
                TransitionManager.beginDelayedTransition(base_view, AutoTransition())
                hiddenView.setVisibility(View.VISIBLE)
                expand_more_email.setImageResource(R.drawable.ic_expand_more_white)
            }
        }

        val chip1 = binding.chip1
        val chip7 = binding.chip7
        val chip30 = binding.chip30
        val caution = binding.caution

        chip1.setOnClickListener {
            gmailAuthorisationPreferenceManager.setInterval("1")
            CoroutineScope(Dispatchers.IO).launch {
                PreferenceDatabaseManager(requireContext()).addPreference(
                    gmailAuthorisationPreferenceManager.PREFS_NAME,
                    gmailAuthorisationPreferenceManager.INTERVAL,
                    gmailAuthorisationPreferenceManager.getInterval()
                )
            }
            chip1.setBackgroundResource(R.drawable.transparent_bg_purple)
            chip7.setBackgroundResource(R.drawable.border)
            chip30.setBackgroundResource(R.drawable.border)
            caution.visibility = View.VISIBLE
        }
        chip7.setOnClickListener {
            gmailAuthorisationPreferenceManager.setInterval("7")
            CoroutineScope(Dispatchers.IO).launch {
                PreferenceDatabaseManager(requireContext()).addPreference(
                    gmailAuthorisationPreferenceManager.PREFS_NAME,
                    gmailAuthorisationPreferenceManager.INTERVAL,
                    gmailAuthorisationPreferenceManager.getInterval()
                )
            }
            chip7.setBackgroundResource(R.drawable.transparent_bg_purple)
            chip1.setBackgroundResource(R.drawable.border)
            chip30.setBackgroundResource(R.drawable.border)
            caution.visibility = View.VISIBLE

        }
        chip30.setOnClickListener {
            gmailAuthorisationPreferenceManager.setInterval("30")
            CoroutineScope(Dispatchers.IO).launch {
                PreferenceDatabaseManager(requireContext()).addPreference(
                    gmailAuthorisationPreferenceManager.PREFS_NAME,
                    gmailAuthorisationPreferenceManager.INTERVAL,
                    gmailAuthorisationPreferenceManager.getInterval()
                )
            }
            chip30.setBackgroundResource(R.drawable.transparent_bg_purple)
            chip1.setBackgroundResource(R.drawable.border)
            chip7.setBackgroundResource(R.drawable.border)
            caution.visibility = View.VISIBLE
        }

        when (gmailAuthorisationPreferenceManager.getInterval()) {
            "1" -> {
                chip1.setBackgroundResource(R.drawable.transparent_bg_purple)
                chip7.setBackgroundResource(R.drawable.border)
                chip30.setBackgroundResource(R.drawable.border)
            }

            "7" -> {
                chip7.setBackgroundResource(R.drawable.transparent_bg_purple)
                chip1.setBackgroundResource(R.drawable.border)
                chip30.setBackgroundResource(R.drawable.border)
            }

            "30" -> {
                chip30.setBackgroundResource(R.drawable.transparent_bg_purple)
                chip1.setBackgroundResource(R.drawable.border)
                chip7.setBackgroundResource(R.drawable.border)
            }
        }

        if (gmailAuthorisationPreferenceManager.getAuthorizationStatus()) {
            grant_access_button.text = "Revoke Access"
            grant_access_button.isEnabled = false
        }

        grant_access_button.setOnClickListener {
            authenticateGmail()
        }

    }


    fun authenticateGmail() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
            .requestServerAuthCode("547046004661-tgps9d2qb5nh777ibvf3us9nv07t40kn.apps.googleusercontent.com")
            .requestScopes(
                Scope("https://mail.google.com"),
                Scope("https://www.googleapis.com/auth/gmail.compose"),
                Scope("https://www.googleapis.com/auth/gmail.insert"),
                Scope("https://www.googleapis.com/auth/gmail.labels"),
                Scope("https://www.googleapis.com/auth/gmail.modify")
            ).build()

        // Initialize GoogleApiClient
        googleApiClient =
            GoogleApiClient.Builder(requireContext()).enableAutoManage(requireActivity(), this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build()

        signIn()
    }

    private fun signIn() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data!!)
            if (result!!.isSuccess) {
                val account = result.signInAccount
                val authCode = account?.serverAuthCode
                Log.d("testing", "Auth code: $authCode")

                sendAuthCodeToServer(authCode!!)

                showSnackbar("Authorisation successful")
            } else {
                Log.e("SignInError", "Sign-in failed: ${result.status}")
                showSnackbar("Sign-in failed")
            }
        }
    }

    fun sendAuthCodeToServer(authCode: String) {

        val apiService =
            commonFunctionManager.retrofitBuilder().create(GmailAuthCodeApiService::class.java)

        val authmap = mapOf(
            "authCode" to authCode, "user" to userGoogleDetailsManager.getUID().toString()
        )

        Log.d("testing", UserGoogleDetailsManager(requireContext()).getUID().toString())

        val authJsonObject = CommonFunctionManager().convertToJson(authmap)

        CoroutineScope(Dispatchers.IO).launch {
            val response = apiService.sendAuthCode(authJsonObject)
            if (response.isSuccessful) {
                gmailAuthorisationPreferenceManager.setAuthorizationStatus(true)
                gmailAuthorisationPreferenceManager.setInterval("7")
                PreferenceDatabaseManager(requireContext()).addPreference(
                    gmailAuthorisationPreferenceManager.PREFS_NAME,
                    gmailAuthorisationPreferenceManager.INTERVAL,
                    gmailAuthorisationPreferenceManager.getInterval()
                )
                PreferenceDatabaseManager(requireContext()).addPreference(
                    gmailAuthorisationPreferenceManager.PREFS_NAME,
                    gmailAuthorisationPreferenceManager.IS_AUTHORIZED,
                    gmailAuthorisationPreferenceManager.getAuthorizationStatus().toString()
                )
                Log.d("testing", response.body().toString())
            } else {
                Log.d("testing", "testing false")
            }
        }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {
        Log.e("ConnectionFailed", "Connection to Google Play Services failed")
        showSnackbar("Connection to Google Play Services failed")
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(
            binding.root, message, Snackbar.LENGTH_SHORT
        ).show()
    }
}