package project.aio.project24.Api.MessagesApi
import com.google.gson.JsonObject
import project.aio.project24.Main.ChatFragment
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MessageApiService {
    @GET("/message/chat/{chatId}")
    suspend fun getMessages(@Path("chatId") chatId: String): Response<List<Message>>

    @POST("/message")
    suspend fun sendMessage(@Body message: JsonObject): Response<Message>
}
