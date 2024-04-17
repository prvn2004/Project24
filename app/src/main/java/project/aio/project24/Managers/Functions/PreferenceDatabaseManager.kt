package project.aio.project24.Managers.Functions

import UserGoogleDetailsManager
import android.content.Context
import com.google.gson.JsonObject
import project.aio.project24.Api.PreferencesApi.PreferenceApiService
import project.aio.project24.Api.PreferencesApi.Preferences
import project.aio.project24.Api.PreferencesApi.PreferencesModelClass
import project.aio.project24.Managers.PreferenceManagers.AuthPreferenceManager
import project.aio.project24.Managers.PreferenceManagers.GmailAuthorisationPreferenceManager
import retrofit2.Response

class PreferenceApiRequests {
    private val commonFunctionManager = CommonFunctionManager()
    private val preferenceApiService = commonFunctionManager.retrofitBuilder().create(
        PreferenceApiService::class.java)

    suspend fun addPreference(participantId: String, preference: JsonObject): Response<PreferencesModelClass> {
        return preferenceApiService.addPreference(participantId, preference)
    }

    suspend fun getPreferences(participantId: String): Response<PreferencesModelClass> {
        return preferenceApiService.getPreferences(participantId)
    }

    suspend fun postPreferences(participantId: String, preferences: JsonObject): Response<PreferencesModelClass> {
        return preferenceApiService.postPreferences(participantId, preferences)
    }

    suspend fun putPreferences(participantId: String, preferenceId: String, preferences: JsonObject): Response<PreferencesModelClass> {
        return preferenceApiService.putPreferences(participantId, preferenceId, preferences)
    }
}

class  PreferenceDatabaseManager(context: Context) {
    private val userGoogleDetailsManager = UserGoogleDetailsManager(context)


    suspend fun addPreference(preferenceName: String, valueName: String, value: String){
        val preference = hashMapOf<String, Any>(
            "preferenceName" to preferenceName,
            "valueName" to valueName,
            "value" to value
        )
        val jsonObject = CommonFunctionManager().convertToJson(preference)
        val preferenceApiRequests = PreferenceApiRequests()
        val participantId = userGoogleDetailsManager.getUID().toString()
        val response = preferenceApiRequests.addPreference(participantId!!, jsonObject)
        if (response.isSuccessful){
            //success
        }else{
            //error
        }
    }

//    suspend fun getPreferences(): PreferencesModelClass {
//        val preferenceApiRequests = PreferenceApiRequests()
//        val participantId = userGoogleDetailsManager.getUID().toString()
//        val response = preferenceApiRequests.getPreferences(participantId!!)
//        if (response.isSuccessful){
////            return response.body()
//        }else{
//            return null
//        }
//    }

    suspend fun postPreferences(preferences: List<Preferences>){
        val preference = hashMapOf<String, Any>(
            "preferences" to preferences
        )
        val jsonObject = CommonFunctionManager().convertToJson(preference)
        val preferenceApiRequests = PreferenceApiRequests()
        val participantId = userGoogleDetailsManager.getUID().toString()
        val response = preferenceApiRequests.postPreferences(participantId!!, jsonObject)
        if (response.isSuccessful){
            //success
        }else{
            //error
        }
    }

    suspend fun putPreferences(preferenceId: String, preferenceName: String, valueName: String, value: String){
        val preference = hashMapOf<String, Any>(
            "preferenceName" to preferenceName,
            "valueName" to valueName,
            "value" to value
        )
        val jsonObject = CommonFunctionManager().convertToJson(preference)
        val preferenceApiRequests = PreferenceApiRequests()
        val participantId = userGoogleDetailsManager.getUID().toString()
        val response = preferenceApiRequests.putPreferences(participantId!!, preferenceId, jsonObject)
        if (response.isSuccessful){
            //success
        }else{
            //error
        }
    }

    suspend fun deletePreference(preferenceId: String) {
        //TODO
    }
}