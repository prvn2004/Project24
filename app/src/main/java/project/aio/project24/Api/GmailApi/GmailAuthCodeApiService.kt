package project.aio.project24.Api.GmailApi

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import java.util.Date

interface GmailAuthCodeApiService {
    @POST("/oauth2code")
    suspend fun sendAuthCode(@Body authCode: JsonObject) : Response<AuthCodeModelClass>
}

data class AuthCodeModelClass(
    @SerializedName("_id")
    val _id: String,
    @SerializedName("authCode")
    val authCode: String,
    @SerializedName("user")
    val user: String,
    @SerializedName("timestamp")
    val timestamp: Date
)