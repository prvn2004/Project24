import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import android.content.Context
import com.google.firebase.auth.FirebaseAuth

class UserGoogleDetailsManager(private val context: Context) {

    fun getGoogleAccount(): GoogleSignInAccount? {
        return GoogleSignIn.getLastSignedInAccount(context)
    }

    fun getProfilePhotoUrl(): String? {
        val account = getGoogleAccount()
        return account?.photoUrl?.toString()
    }

    fun getUID(): String {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }

    fun getDisplayName(): String? {
        val account = getGoogleAccount()
        return account?.displayName
    }

    fun getEmail(): String? {
        val account = getGoogleAccount()
        return account?.email
    }

}