package project.aio.project24.Api.ChatsApi

import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import project.aio.project24.Api.MessagesApi.MessageApiService
import project.aio.project24.Main.ChatFragment
import retrofit2.Response

class ChatRespository(private val apiService: ChatApiService) {

    suspend fun startChat(chat: JsonObject) : Response<Chat> {
        return withContext(Dispatchers.IO) {
            apiService.startChat(chat)
        }
    }

    suspend fun getChats(participantId: String) : Response<List<Chat>> {
        return withContext(Dispatchers.IO) {
            apiService.getChats(participantId)
        }
    }
}