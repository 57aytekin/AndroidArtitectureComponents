package com.example.yeniappwkotlin.data.network.repositories

import com.example.yeniappwkotlin.data.db.database.AppDatabase
import com.example.yeniappwkotlin.data.db.entities.User
import com.example.yeniappwkotlin.data.network.MyApi
import com.example.yeniappwkotlin.data.network.SafeApiRequest
import com.example.yeniappwkotlin.data.network.responses.AuthResponse
import com.example.yeniappwkotlin.data.network.responses.EditResponse

class UserRepository(
    private val api : MyApi,
    private val db : AppDatabase
) : SafeApiRequest() {

    suspend fun userLogin(email: String, password: String): AuthResponse{
        return apiRequest { api.userLogin(email, password) }
    }
    suspend fun userRegister(name : String, email: String, password: String) : AuthResponse{
        return apiRequest { api.userRegister(name, email, password) }
    }
    suspend fun savePost(user_id : Int, share_post: String, like_count: Int, comment_count : Int) : EditResponse {
        return apiRequest { api.savePost(user_id, share_post, like_count, comment_count) }
    }
    suspend fun saveUser(user : User) = db.getUserDao().upsert(user)

    fun getUser() = db.getUserDao().getUser()
}