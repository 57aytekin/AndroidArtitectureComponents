package com.vaveylax.yeniappwkotlin.data.network.repositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vaveylax.yeniappwkotlin.R
import com.vaveylax.yeniappwkotlin.data.db.database.AppDatabase
import com.vaveylax.yeniappwkotlin.data.db.entities.Post
import com.vaveylax.yeniappwkotlin.data.network.MyApi
import com.vaveylax.yeniappwkotlin.data.network.SafeApiRequest
import com.vaveylax.yeniappwkotlin.data.network.responses.PostLikesResponse
import com.vaveylax.yeniappwkotlin.util.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

@Suppress("CAST_NEVER_SUCCEEDS", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class PostRepository(
    private val api : MyApi,
    private val db : AppDatabase,
    private val context : Context
) : SafeApiRequest() {
    private val posts = MutableLiveData<List<Post>>()
    private val MINUMUM_INTERVAL = 5
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

    suspend fun getPostData(user_id: Int, page : Int, row_per_page : Int) : List<Post>{
        return apiRequest { api.getPost(user_id, page, row_per_page) }
    }
    suspend fun getUserPosts(user_id: Int) : List<Post>{
        return apiRequest { api.getUserPosts(user_id) }
    }

    private suspend fun fetchPosts(user_id: Int, page : Int, row_per_page : Int){
        val lastSavedAt = PrefUtils.with(context).getLastSavedAt(KEY_SAVED_AT)
        if((lastSavedAt == null ||lastSavedAt.isEmpty()) || isFetchNeeded(context,KEY_SAVED_AT, MINUMUM_INTERVAL)){
            try {
                val response = apiRequest { api.getPost(user_id, page, row_per_page) }
                db.getUserDao().deletePost()
                posts.postValue(response)
            } catch (e: ApiException) {
                Log.d("POST_1",e.message!!)
            } catch (e: NoInternetException) {
                Coroutines.main { context.toast(context.getString(R.string.check_internet)) }
                posts.postValue(db.getPostDao().getLocalPost())
            } catch (e : Exception){
                Log.d("POST_3",e.message!!)
            }
        }
    }
    suspend fun getPosts(user_id: Int, page : Int, row_per_page : Int) : LiveData<List<Post>>{
        return withContext(Dispatchers.IO){
            fetchPosts(user_id, page, row_per_page)
            db.getPostDao().getPosts()
        }
    }

    suspend fun pushNotification(user_name: String, other_user_name: String, commentName: String, durum: Int) : String{
        return apiRequest { api.pushNotification(user_name, other_user_name, commentName, durum) }
    }

    suspend fun getLocalPost(): List<Post>{
        return db.getPostDao().getLocalPost()
    }

    suspend fun updateLikeCounts(post_id : Int, user_id: Int , like_count : Int, begeniDurum: Int) = apiRequest { api.updateLikeCount(post_id, user_id, like_count, begeniDurum) }

    suspend fun saveUserPostLikes(user_id : Int, post_id : Int, begeniDurum : Int, likeCount: Int, post_sahibi_id: Int) : PostLikesResponse {
        return apiRequest { api.saveUserPostLikes(user_id, post_id, begeniDurum, likeCount, post_sahibi_id) }
    }

    fun updateLocalPostCount(likeCount : Int, begeniDurum: Int, id : Int){
        CoroutineScope(Dispatchers.IO).launch {
            db.getPostDao().updateLocalPostCount(likeCount, begeniDurum, id)
        }
    }

    fun savePost(post : List<Post>){
        CoroutineScope(Dispatchers.IO).launch {
            db.getPostDao().savePosts(post)
        }
    }

    suspend fun getLocalUserPost(userId: Int) : List<Post>{
        return db.getPostDao().getLocalUserPost(userId)
    }

}
