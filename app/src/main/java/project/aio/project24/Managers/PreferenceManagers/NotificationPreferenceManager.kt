package project.aio.project24.Managers.PreferenceManagers
import android.content.Context
import android.content.SharedPreferences

class NotificationPreferenceManager(context: Context) {
    val NotificationPreferences = "NotificationPreferences"
    val sharedPreferences: SharedPreferences = context.getSharedPreferences(NotificationPreferences, Context.MODE_PRIVATE)
    val IS_ENABLED = "IS_ENABLED"
    val FCM_TOKEN_VALUE = "FCM_TOKEN_VALUE"

    fun setNotificationEnabled(isEnabled: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(IS_ENABLED, isEnabled)
        editor.apply()
    }

    fun isNotificationEnabled(): Boolean {
        return sharedPreferences.getBoolean(IS_ENABLED, false)
    }

    fun setFcmTokenValue(token: String) {
        val editor = sharedPreferences.edit()
        editor.putString(FCM_TOKEN_VALUE, token)
        editor.apply()
    }

    fun getFcmTokenValue(): String? {
        return sharedPreferences.getString(FCM_TOKEN_VALUE, null)
    }

    fun deleteNotificationValues() {
        val editor = sharedPreferences.edit()
        editor.remove(FCM_TOKEN_VALUE)
        editor.remove("FCM_TOKEN_VALUE")
        editor.apply()
    }
}