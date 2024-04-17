package project.aio.project24.Api.UserApi

import com.google.gson.JsonObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApiService {
@POST("/user")
suspend fun createUser(@Body user: JsonObject): Response<UserResponse>
}


// Request Body
data class User(
    val username: String,
    val useremail: String,
    val userimage: String,
    val useruid: String
)

// Response Body
data class UserResponse(
    val _id: String,
    val username: String,
    val useremail: String,
    val userimage: String,
    val useruid: String
)