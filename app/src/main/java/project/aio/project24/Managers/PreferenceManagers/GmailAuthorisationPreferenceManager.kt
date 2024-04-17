package project.aio.project24.Managers.PreferenceManagers
import android.content.Context
import android.content.SharedPreferences

class GmailAuthorisationPreferenceManager(context: Context) {
    public val PREFS_NAME = "gmail_auth_prefs"
    public val IS_AUTHORIZED = "is_authorized"
    public val INTERVAL = "interval"
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun setInterval(interval: String) {
        val editor = prefs.edit()
        editor.putString(INTERVAL, interval)
        editor.apply()
    }

    //1 -> 1 day
    //7 -> 1 week
    //30 -> 1 month
    fun getInterval(): String {
        return prefs.getString(INTERVAL, "7")!!
    }

    // Create or Update the authorization status
    fun setAuthorizationStatus(isAuthorized: Boolean) {
        val editor = prefs.edit()
        editor.putBoolean(IS_AUTHORIZED, isAuthorized)
        editor.apply()
    }

    // Read the authorization status
    fun getAuthorizationStatus(): Boolean {
        return prefs.getBoolean(IS_AUTHORIZED, false)
    }

    // Delete the authorization status
    fun deleteAuthorizationStatus() {
        val editor = prefs.edit()
        editor.remove(IS_AUTHORIZED)
        editor.apply()
    }
}