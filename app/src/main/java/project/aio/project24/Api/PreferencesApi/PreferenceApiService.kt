package project.aio.project24.Api.PreferencesApi

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import project.aio.project24.Api.UserApi.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.sql.Timestamp
import java.util.prefs.Preferences

interface PreferenceApiService {

    //post one preference to preferences
    @POST("/preferences/addonepreference/{participantId}")
    suspend fun addPreference(@Path("participantId") participantId: String,  @Body preference: JsonObject): Response<PreferencesModelClass>

//    @DELETE("/preferences/{_id}")
//    suspend fun deletePreference(@Path("_id") _id: String): Response<Unit>

    //get all preferences of a user using the user id
    @GET("/preferences/getallpreferences/{participantId}")
    suspend fun getPreferences(@Path("participantId") participantId: String): Response<PreferencesModelClass>

    //post many preference all at once
    @POST("/preferences/addmanypreferences/{participantId}")
    suspend fun postPreferences(@Path("participantId") participantId: String, @Body preferences: JsonObject): Response<PreferencesModelClass>

    //update once preference using participant id and preferenceid
    @PUT("/preferences/updatepreference/{participantId}/{preferenceId}")
    suspend fun putPreferences(@Path("participantId") participantId: String, @Path("preferenceId") preferenceId: String, @Body preferences: JsonObject): Response<PreferencesModelClass>
}

data class PreferencesModelClass(
    @SerializedName("_id")
    val _id: String = "",  //auto set by the database
    @SerializedName("preferences")
    val preferences: List<project.aio.project24.Api.PreferencesApi.Preferences>,  //list of preferences by user
    @SerializedName("timestamp")
    val timestamp: Timestamp = Timestamp(0), //auto set by the database
    @SerializedName("user")
    val participantId: String  //user uid
)

data class Preferences(
    @SerializedName("_id")
    val _id: String = "", //auto set by the database
    @SerializedName("preferenceName")
    val preferenceName: String, //name of the preference
    @SerializedName("valueName")
    val valueName: String,
    @SerializedName("value")
    val value: String //value of the preference
)