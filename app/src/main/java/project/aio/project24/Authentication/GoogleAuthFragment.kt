package project.aio.project24.Authentication

import UserGoogleDetailsManager
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import project.aio.project24.Api.UserApi.User
import project.aio.project24.Api.UserApi.UserApiService
import project.aio.project24.Main.MainActivity
import project.aio.project24.Managers.Functions.CommonFunctionManager
import project.aio.project24.Managers.Functions.Constants
import project.aio.project24.Managers.Functions.PreferenceDatabaseManager
import project.aio.project24.Managers.PreferenceManagers.AuthPreferenceManager
import project.aio.project24.R
import project.aio.project24.databinding.FragmentGoogleAuthBinding
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GoogleAuthFragment : Fragment() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var binding: FragmentGoogleAuthBinding

    private lateinit var authPreferenceManager: AuthPreferenceManager
    private lateinit var userGoogleDetailsManager: UserGoogleDetailsManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGoogleAuthBinding.inflate(inflater, container, false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.googleloginbutton.setOnClickListener {
            signIn()
        }

        authPreferenceManager = AuthPreferenceManager(requireContext())
        userGoogleDetailsManager = UserGoogleDetailsManager(requireContext())
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (task.isSuccessful) {
                val account = task.result
                firebaseAuthWithGoogle(account)
            } else {
                Snackbar.make(binding.root, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount?) {
    val credential = GoogleAuthProvider.getCredential(account?.idToken, null)
    firebaseAuth.signInWithCredential(credential)
        .addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                val user = firebaseAuth.currentUser

                writeToDatabase()
            } else {
                // Display a Snackbar message when the authentication fails
                Snackbar.make(binding.root, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
            }
        }
}

private fun writeToDatabase(){

    val apiService = CommonFunctionManager().retrofitBuilder().create(UserApiService::class.java)

    val user = mapOf(
        "username" to userGoogleDetailsManager.getDisplayName().toString(),
        "useremail" to userGoogleDetailsManager.getEmail().toString(),
        "userimage" to userGoogleDetailsManager.getProfilePhotoUrl().toString(),
        "useruid" to userGoogleDetailsManager.getUID().toString()
    )

    val gson = Gson()
    val userString = gson.toJson(user)
    val userJson = JsonParser.parseString(userString).asJsonObject

    CoroutineScope(Dispatchers.IO).launch {
        val response = apiService.createUser(userJson)

        if (response.isSuccessful) {
            val userResponse = response.body()
            if (userResponse != null) {
                authPreferenceManager.setLoginStatus(true)
                authPreferenceManager.setCurrentAuthId(userResponse._id)

                GlobalScope.launch{
                PreferenceDatabaseManager(requireContext()).addPreference(authPreferenceManager.PREFS_NAME, authPreferenceManager.CURRENT_ID, authPreferenceManager.getCurrentAuthId().toString())
                PreferenceDatabaseManager(requireContext()).addPreference(authPreferenceManager.PREFS_NAME, authPreferenceManager.IS_LOGIN, authPreferenceManager.getLoginStatus().toString())
                }

                val intent = Intent(context, MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }

        } else {
            Snackbar.make(binding.root, "Failed to write to database.", Snackbar.LENGTH_SHORT).show()
        }
    }
}

    companion object {
        private const val RC_SIGN_IN = 123
    }
}