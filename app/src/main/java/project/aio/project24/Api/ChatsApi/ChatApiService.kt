package project.aio.project24.Api.ChatsApi

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import project.aio.project24.Api.MessagesApi.Message
import project.aio.project24.Api.UserApi.User
import project.aio.project24.Main.ChatFragment
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.Date

interface ChatApiService {
//    @GET("/chat/{participantId}")
//    suspend fun getChats(@Path("participantId") participantId: String): Response<List<Chats>>

    @POST("/chat")
    suspend fun startChat(@Body chat: JsonObject): Response<Chat>

    @GET("/chat/{participantId}")
    suspend fun getChats(@Path("participantId") participantId: String): Response<List<Chat>>
}

class Chat {
    @SerializedName("participantId")
    var participantId: List<User> = emptyList()
    @SerializedName("_id")
    var _id: String = ""
    @SerializedName("messages")
    var messages: List<Message> = emptyList()
    @SerializedName("private_mode")
    var private_mode : Boolean = false
    @SerializedName("timestamp")
    var timestamp : Date = Date()
    @SerializedName("chatTitle")
    var chatTitle : String = ""
}
