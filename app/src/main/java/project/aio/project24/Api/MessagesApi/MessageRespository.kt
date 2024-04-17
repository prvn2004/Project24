package project.aio.project24.Api.MessagesApi

import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import project.aio.project24.Main.ChatFragment
import retrofit2.Response

class MessageRepository(private val apiService: MessageApiService) {
    suspend fun getMessages(chatId: String): Response<List<Message>> {
        return withContext(Dispatchers.IO) {
            apiService.getMessages(chatId)
        }
    }

    suspend fun sendMessage(message: JsonObject) : Response<Message> {
        return withContext(Dispatchers.IO) {
            apiService.sendMessage(message)
        }
    }
}
