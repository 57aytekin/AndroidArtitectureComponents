package com.vaveylax.yeniappwkotlin.data.network.repositories

import com.vaveylax.yeniappwkotlin.data.db.database.AppDatabase
import com.vaveylax.yeniappwkotlin.data.db.entities.Post
import com.vaveylax.yeniappwkotlin.data.db.entities.User
import com.vaveylax.yeniappwkotlin.data.network.MyApi
import com.vaveylax.yeniappwkotlin.data.network.SafeApiRequest
import com.vaveylax.yeniappwkotlin.data.network.responses.AuthResponse
import com.vaveylax.yeniappwkotlin.data.network.responses.CommentResponse
import com.vaveylax.yeniappwkotlin.data.network.responses.EditResponse

class UserRepository(
    private val api : MyApi,
    private val db : AppDatabase
) : SafeApiRequest() {

    suspend fun userLogin(email: String, password: String): AuthResponse{
        return apiRequest { api.userLogin(email, password) }
    }
    suspend fun userRegister(userName : String, firsName : String,lastName : String, email: String, phone: String, password: String, paths: String, is_social_account : Int) : AuthResponse{
        return apiRequest { api.userRegister(userName, firsName, lastName, email, phone, password, paths, is_social_account) }
    }
    suspend fun savePost(user_id : Int, share_post: String, like_count: Int, comment_count : Int) : EditResponse {
        return apiRequest { api.savePost(user_id, share_post, like_count, comment_count) }
    }

    suspend fun updateIsLogin(user_id: Int, is_login: Int): CommentResponse{
        return apiRequest { api.updateWhoIsTalking(user_id, is_login) }
    }

    suspend fun updateUserLoginStatu(user_id: Int, is_login: Int): CommentResponse{
        return apiRequest { api.updateUserLoginStatu(user_id, is_login) }
    }

    suspend fun updateTokenIsLoginLastLogin(user_id: Int, token: String, is_login: Int): CommentResponse{
        return apiRequest { api.updateTokenIsLoginLastLogin(user_id, token, is_login) }
    }

    suspend fun updateUserProfile(user_id: Int, user_name: String, user_first_name: String, user_last_name: String, image: String): CommentResponse{
        return apiRequest { api.updateUserProfile(user_id, user_name, user_first_name, user_last_name, image) }
    }
    suspend fun saveUser(user : User) = db.getUserDao().upsert(user)
    suspend fun saveUserPost(post: Post) = db.getUserDao().saveUserPost(post)
    fun getUser() = db.getUserDao().getUser()

    suspend fun updateLocalUser(userName : String, firstName: String, lastName : String, paths : String, userId: Int){
        if (paths == "null"){
            db.getUserDao().updateUserNames(userName, firstName, lastName)
        }else{
            val userPath = "image/$userName.jpg"
            db.getUserDao().updateUserAllData(userName, firstName, lastName, userPath)
        }
    }

    suspend fun getLocalUserPost(userId: Int) : List<Post>{
        return db.getPostDao().getLocalUserPost(userId)
    }

    fun deleteUser() = db.getUserDao().deleteUser()
    fun deletePost() = db.getUserDao().deletePost()
    fun deleteLikes() = db.getUserDao().deleteLikes()
    fun deleteMessageList() = db.getUserDao().deleteMessageList()


}