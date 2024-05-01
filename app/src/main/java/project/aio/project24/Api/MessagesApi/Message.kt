package project.aio.project24.Api.MessagesApi

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Message(
    @SerializedName("chatId")
    val chatId: String,
    @SerializedName("content")
    val content: String,
    @SerializedName("isBot")
    val isBot: Boolean,
    @SerializedName("timestamp")
    val timestamp: Date,
    val isPending : Boolean = false
)