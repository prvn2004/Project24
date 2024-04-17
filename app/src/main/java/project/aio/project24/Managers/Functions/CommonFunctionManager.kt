package project.aio.project24.Managers.Functions


import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import project.aio.project24.R
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CommonFunctionManager {
    fun convertToJson(data: Map<String, Any?>): JsonObject {
        val gson = Gson()
        val chatString = gson.toJson(data)
        return JsonParser.parseString(chatString).asJsonObject
    }

    fun retrofitBuilder(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://51.20.89.113:80")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun convertStringToJson(input: String): JsonObject {
        return JsonParser.parseString(input).asJsonObject
    }

}

