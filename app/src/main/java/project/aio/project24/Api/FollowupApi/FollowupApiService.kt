package project.aio.project24.Api.FollowupApi

import com.google.gson.annotations.SerializedName
import project.aio.project24.Api.ChatsApi.Chat
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.Date

interface FollowupApiService {

    @GET("/followup/getall/{participantId}")
    suspend fun getFollowups(@Path("participantId") participantId: String): Response<List<FollowupsModel>>
}

data class ReferenceModel(
    @SerializedName("email")
    val email: String?
)

data class FollowupElement(
    @SerializedName("_id")
    val _id: String,
    @SerializedName("timestamp")
    val timestamp: Date,
    @SerializedName("entities")
    val entities: List<String>?,
    @SerializedName("reference")
    val reference: String
)

data class ReferenceMail(
    @SerializedName("from")
    val from: String = "",
    @SerializedName("date")
    val date: String = "",
    @SerializedName("subject")
    val subject: String = ""
)

data class FollowupsModel(
    @SerializedName("_id")
    val _id: String = "",
    @SerializedName("timestamp")
    val timestamp: Date = Date(),
    @SerializedName("followup_history")
    val followup_history: List<String>? = emptyList(),
    @SerializedName("participant")
    val participant: String = "",
    @SerializedName("autoResponses")
    val autoResponses: List<String>? = emptyList(),
    @SerializedName("title")
    val title: String = "",
    val referenceMail: List<ReferenceMail> = emptyList(),
)