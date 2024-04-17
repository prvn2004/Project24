package project.aio.project24.Api.MessagesApi

import androidx.lifecycle.ViewModel
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import project.aio.project24.Main.ChatFragment
import retrofit2.Response

class MessageViewModel(private val repository: MessageRepository) : ViewModel() {

    suspend fun getMessages(chatId: String): Response<List<Message>> {
        return withContext(Dispatchers.IO) {
            repository.getMessages(chatId)
        }
    }

    suspend fun sendMessage(message: JsonObject) : Response<Message>{
        return withContext(Dispatchers.IO) {
            repository.sendMessage(message)
        }
    }
}
