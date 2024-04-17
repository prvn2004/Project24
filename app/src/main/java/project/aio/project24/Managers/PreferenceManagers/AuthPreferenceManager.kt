package project.aio.project24.Managers.PreferenceManagers

import android.content.Context
import android.content.SharedPreferences

class AuthPreferenceManager(context: Context) {
     val PREFS_NAME = "auth_prefs"
     val IS_LOGIN = "is_login"
    val CURRENT_ID = "current_id"
     val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Create or Update login status
    fun setLoginStatus(isLogin: Boolean) {
        val editor = prefs.edit()
        editor.putBoolean(IS_LOGIN, isLogin)
        editor.apply()
    }

    fun setCurrentAuthId(id: String) {
        val editor = prefs.edit()
        editor.putString(CURRENT_ID, id)
        editor.apply()
    }

    fun getCurrentAuthId(): String? {
        return prefs.getString(CURRENT_ID, null)
    }

    // Read login status
    fun getLoginStatus(): Boolean {
        return prefs.getBoolean(IS_LOGIN, false)
    }

    // Delete login status
    fun deleteLoginStatus() {
        val editor = prefs.edit()
        editor.remove(IS_LOGIN)
        editor.apply()
    }
}