package com.example.yeniappwkotlin.data.network.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.yeniappwkotlin.data.db.entities.Post
import com.example.yeniappwkotlin.data.network.MyApi
import com.example.yeniappwkotlin.data.network.SafeApiRequest
import com.example.yeniappwkotlin.data.network.responses.EditResponse
import com.example.yeniappwkotlin.data.network.responses.PostLikesResponse
import com.example.yeniappwkotlin.data.network.responses.PostResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PostRepository(
    private val api : MyApi
) : SafeApiRequest() {
    suspend fun getPosts(user_id: Int)  = apiRequest { api.getPost(user_id) }

    suspend fun updateLikeCounts(post_id : Int, user_id: Int , like_count : Int, begeniDurum: Int) = apiRequest { api.updateLikeCount(post_id, user_id, like_count, begeniDurum) }

    suspend fun saveUserPostLikes(user_id : Int, post_id : Int, begeniDurum : Int) : PostLikesResponse {
        return apiRequest { api.saveUserPostLikes(user_id, post_id, begeniDurum) }
    }
    suspend fun getUserPostLikes(user_id: Int) : PostLikesResponse{
        return apiRequest { api.getUserPostLikes(user_id) }
    }
}