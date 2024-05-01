package project.aio.project24.Managers.Functions

import UserGoogleDetailsManager
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import project.aio.project24.Api.ChatsApi.Chat
import project.aio.project24.Api.ChatsApi.ChatViewModel
import project.aio.project24.Main.ChatFragment
import project.aio.project24.Main.ChatHistoryAdapter
import project.aio.project24.Managers.PreferenceManagers.AuthPreferenceManager
import retrofit2.Response

class ChatManager(
    private val chatViewModel: ChatViewModel,
    private val root: View,
    private val userGoogleDetailsManager: UserGoogleDetailsManager
) {

    var currentChatId: String = ""

    fun createNewChat(private_mode: Boolean): Deferred<String> {
        val chatData = mapOf(
            "participantId" to userGoogleDetailsManager.getUID(),
            "private_mode" to "$private_mode"
        )
        val chatJson = convertToJson(chatData)

        return CoroutineScope(Dispatchers.Main).async {
            val response = chatViewModel.startChat(chatJson)
            handleResponse(response)
            currentChatId
        }
    }

    fun fetchAllChats(participantId: String, rvChatHistory: RecyclerView) {
        CoroutineScope(Dispatchers.IO).launch {
            val response = chatViewModel.getChats(participantId)
            if (response.isSuccessful) {
                val chats = response.body()?.sortedByDescending { it.timestamp }

                withContext(Dispatchers.Main) {
                    (rvChatHistory.adapter as ChatHistoryAdapter).submitList(chats)
                }
            } else {
                // Handle error
            }
        }
    }


    private fun convertToJson(data: Map<String, String?>): JsonObject {
        val gson = Gson()
        val chatString = gson.toJson(data)
        return JsonParser.parseString(chatString).asJsonObject
    }

    private fun handleResponse(response: Response<Chat>) {
        if (response.isSuccessful) {
            val responseBody = response.body()
            if (responseBody != null) {
                Log.d("testin", "createNewChat: ${responseBody._id}")
                currentChatId = responseBody._id
            } else {
                showSnackbar("Failed to create chat")
            }
        } else {
            showSnackbar("Failed to create chat")
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(
            root,
            message,
            Snackbar.LENGTH_SHORT
        ).show()
    }


}