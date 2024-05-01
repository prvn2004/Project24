package project.aio.project24.Managers.Functions

import UserGoogleDetailsManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.WebSocket
import project.aio.project24.Api.MessagesApi.Message
import project.aio.project24.Api.MessagesApi.MessageViewModel
import project.aio.project24.Api.Sockets.SocketManager
import project.aio.project24.Main.MessageAdapter
import retrofit2.Response
import java.util.Date

class MessageManager(
    private val messageViewModel: MessageViewModel,
    private val adapter: MessageAdapter,
    private val rvMessages: RecyclerView,
    private val root: View,
    val messageList: MutableList<Message>,
    val view : LinearLayout
) {

    fun sendMessage(
        messageContent: String,
        currentChatId: String,
        context: Context,
        socketManager: SocketManager
    ) {
        if (messageContent.isEmpty()) return

        val currentTime = Date()
        val message = Message(currentChatId, messageContent, false, currentTime)
        addMessageToList(message)

        adapter.addMessage(Message("", "", false, Date(), true))

        val messageData = mapOf(
            "chatId" to currentChatId, "content" to messageContent, "isBot" to false
        )
        val dataWithParticipantId = mapOf(
            "participantId" to UserGoogleDetailsManager(context).getUID().toString(),
            "query" to messageData
        )

        val messageJson = convertToJson(dataWithParticipantId)

//            val response = messageViewModel.sendMessage(messageJson)

        CoroutineScope(Dispatchers.IO).launch {
            socketManager.sendMessage(messageJson.toString())
            view.visibility  = View.GONE
        }
    }

    private fun addMessageToList(message: Message) {
        messageList.add(message)
        adapter.notifyItemInserted(messageList.size - 1)
        rvMessages.scrollToPosition(messageList.size - 1)
    }

    private fun convertToJson(data: Map<String, Any>): JsonObject {
        val gson = Gson()
        val messageString = gson.toJson(data)
        return JsonParser.parseString(messageString).asJsonObject
    }

//    private fun handleResponse(response: Response<Message>) {
//    if (response.isSuccessful) {
//        val responseBody = response.body()
//        if (responseBody != null) {
//            val botMessage = Message(
//                responseBody.chatId,
//                responseBody.content,
//                true,
//                responseBody.timestamp
//            )
//            addMessageToList(botMessage)
//        } else {
//            showSnackbar("Failure to add message in server")
//        }
//    } else {
//        when (response.code()) {
//            404 -> showSnackbar("Gemini model failure, retry after 60 seconds")
//            500 -> showSnackbar( "Server failure")
//            else -> showSnackbar("Failed to send message")
//        }
//    }

     fun handleResponse(code: Int, response: Message) {
        if (code == 201) {
            if (response != null) {
                val botMessage = Message(
                    response.chatId, response.content, true, response.timestamp
                )
                Handler(Looper.getMainLooper()).post {
                    addMessageToList(botMessage)
                }
            } else {
                showSnackbar("Failure to add message in server")
            }
        } else {
            when (code) {
                404 -> showSnackbar("Gemini model failure, retry after 60 seconds")
                500 -> showSnackbar("Server failure")
                else -> showSnackbar("Failed to send message")
            }
        }
    }

    fun handleAllMessages(code: Int, response: Array<Message>) {
        if (code == 201) {
            val messages = response
            for (message in messages) {
                val chatId = message.chatId
                val content = message.content
                val isBot = message.isBot
                val timestamp = message.timestamp

                val message = Message(chatId, content, isBot, timestamp)
                addMessageToList(message)
            }
        } else {
            when (code) {
                404 -> showSnackbar("Gemini model failure, retry after 60 seconds")
                500 -> showSnackbar("Server failure")
                else -> showSnackbar("Failed to get messages")
            }
        }
    }

    private fun showSnackbar(message: String) {
        Handler(Looper.getMainLooper()).post {
            Snackbar.make(
                root, message, Snackbar.LENGTH_LONG
            ).show()
        }
    }
}