package com.example.yeniappwkotlin.data.network.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.yeniappwkotlin.data.db.database.AppDatabase
import com.example.yeniappwkotlin.data.db.entities.Post
import com.example.yeniappwkotlin.data.network.MyApi
import com.example.yeniappwkotlin.data.network.SafeApiRequest
import com.example.yeniappwkotlin.data.network.responses.PostLikesResponse
import com.example.yeniappwkotlin.util.Coroutines
import com.example.yeniappwkotlin.util.PrefUtils
import com.example.yeniappwkotlin.util.isFetchNeeded
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

@Suppress("CAST_NEVER_SUCCEEDS", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class PostRepository(
    private val api : MyApi,
    private val db : AppDatabase,
    private val context : Context
) : SafeApiRequest() {
    private val posts = MutableLiveData<List<Post>>()
    private val MINUMUM_INTERVAL = 1
    private val KEY_SAVED_AT = "post_key_saved_at"
    init {
        posts.observeForever {
            savePosts(it)
        }
    }

    private var lastDate = SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.ENGLISH).format(Date())

    private fun savePosts(posts : List<Post>){
        Coroutines.io {
            PrefUtils.with(context).saveLastSavedAt(KEY_SAVED_AT,lastDate.toString())
            db.getPostDao().saveAllPosts(posts)
        }
    }

    private suspend fun fetchPosts(user_id: Int){
        val lastSavedAt = PrefUtils.with(context).getLastSavedAt(KEY_SAVED_AT)
        if(lastSavedAt == null || isFetchNeeded(context,KEY_SAVED_AT, MINUMUM_INTERVAL)){
            val response = apiRequest { api.getPost(user_id) }
            posts.postValue(response)
        }
    }
    suspend fun getPosts(user_id: Int) : LiveData<List<Post>>{
        return withContext(Dispatchers.IO){
            fetchPosts(user_id)
            db.getPostDao().getPosts()
        }
    }

    suspend fun getLocalPost(user_id: Int): LiveData<List<Post>>{
        return withContext(Dispatchers.IO){
            db.getPostDao().getPosts()
        }
    }

    suspend fun updateLikeCounts(post_id : Int, user_id: Int , like_count : Int, begeniDurum: Int) = apiRequest { api.updateLikeCount(post_id, user_id, like_count, begeniDurum) }

    suspend fun saveUserPostLikes(user_id : Int, post_id : Int, begeniDurum : Int, likeCount: Int) : PostLikesResponse {
        return apiRequest { api.saveUserPostLikes(user_id, post_id, begeniDurum, likeCount) }
    }

    fun updateLocalPostCount(likeCount : Int, begeniDurum: Int, id : Int){
        CoroutineScope(Dispatchers.IO).launch {
            db.getPostDao().updateLocalPostCount(likeCount, begeniDurum, id)
        }
    }

}
