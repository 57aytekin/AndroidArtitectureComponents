package com.example.yeniappwkotlin.data.network.repositories

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.yeniappwkotlin.data.db.entities.Post
import com.example.yeniappwkotlin.data.network.MyApi
import com.example.yeniappwkotlin.data.network.SafeApiRequest
import com.example.yeniappwkotlin.data.network.responses.EditResponse
import com.example.yeniappwkotlin.data.network.responses.PostResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PostRepository(
    private val api : MyApi
) : SafeApiRequest() {
    suspend fun getPosts()  = apiRequest { api.getPost() }

    suspend fun updateLikeCounts(like_count : Int, post_id : Int ) = apiRequest { api.updateLikeCount(like_count, post_id) }
}