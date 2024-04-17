package project.aio.project24.Managers.PreferenceManagers

import android.content.Context
import android.content.SharedPreferences

class ChatCurrentPreferenceManager(context: Context) {
    val PREFS_NAME = "ChatPreferences"
    val CURRENT_VALUE = "current_value"

    //0 -> GLOBAL
    //1 -> PRIVATE

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Create
    fun setCurrentValue(value: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean(CURRENT_VALUE, value)
        editor.apply()
    }

    // Read
    fun getCurrentValue(): Boolean {
        return sharedPreferences.getBoolean(CURRENT_VALUE, false) // default value is 0 (global)
    }

    // Update
    fun updateCurrentValue(value: Boolean) {
        setCurrentValue(value)
    }

    // Delete
    fun deleteCurrentValue() {
        val editor = sharedPreferences.edit()
        editor.remove("current_value")
        editor.apply()
    }
}