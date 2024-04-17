package project.aio.project24.Api.ChatsApi

import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import project.aio.project24.Api.MessagesApi.MessageRepository
import project.aio.project24.Main.ChatFragment
import retrofit2.Response

class ChatViewModel(private val repository: ChatRespository) : ViewModel() {

    suspend fun startChat(chat: JsonObject) : Response<Chat> {
        return withContext(Dispatchers.IO) {
            repository.startChat(chat)
        }
    }

    suspend fun getChats(participantId: String) : Response<List<Chat>> {
        return withContext(Dispatchers.IO) {
            repository.getChats(participantId)
        }
    }
}
