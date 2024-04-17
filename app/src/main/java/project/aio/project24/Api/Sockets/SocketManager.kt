package project.aio.project24.Api.Sockets

import android.util.Log
import com.google.gson.JsonObject
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONException
import org.json.JSONObject
import project.aio.project24.Managers.Functions.CommonFunctionManager
import java.net.URISyntaxException

object SocketManager {
    var socket: Socket? = null

    init {
        try {
            socket = IO.socket("http://51.20.89.113:80")
        } catch (e: URISyntaxException) {
            Log.e("testing", "Failed to connect to socket")
            e.printStackTrace()
        }
    }

    fun connect() {
        socket?.connect()
    }

    fun disconnect() {
        socket?.disconnect()
    }

    fun isConnected(): Boolean {
        return socket?.connected() ?: false
    }

    fun onMessageReceived(listener: (Int, JSONObject) -> Unit) {
        socket?.on("newMessage") { args ->
            val code = args[0] as Int
            val message = args[1] as JSONObject

            Log.d("testing", "Received message: $message")

            listener(code, message)
        }
    }

    suspend fun sendMessage(message: String) {
        Log.d("testing", "Sending message: $message")
        socket?.emit("newMessage", message)
    }
}