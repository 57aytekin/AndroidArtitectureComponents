package com.example.yeniappwkotlin.data.network.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.yeniappwkotlin.data.db.database.AppDatabase
import com.example.yeniappwkotlin.data.db.entities.Likes
import com.example.yeniappwkotlin.data.network.MyApi
import com.example.yeniappwkotlin.data.network.SafeApiRequest
import com.example.yeniappwkotlin.util.Coroutines
import com.example.yeniappwkotlin.util.PrefUtils
import com.example.yeniappwkotlin.util.isFetchNeeded
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class LikesRepository(
    val api : MyApi,
    val db : AppDatabase,
    val context : Context
) : SafeApiRequest() {
    private val likes = MutableLiveData<List<Likes>>()
    private val MINUMUM_INTERVAL = -1
    private val KEY_SAVED_AT = "likes_key_saved_at"

    init {
        likes.observeForever {
            saveLikes(it)
        }
    }
    private var likesLastDate = SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.ENGLISH).format(Date())
    private fun saveLikes(likes : List<Likes>){
        Coroutines.io {
            try{
                PrefUtils.with(context).saveLastSavedAt(KEY_SAVED_AT,likesLastDate.toString())
                db.getLikesDao().saveAllLikes(likes)
            }catch (e : Exception){
                e.printStackTrace()
            }
        }
    }

    suspend fun getLikes(user_id : Int): LiveData<List<Likes>>{
        return withContext(Dispatchers.IO){
            fetchLikes(user_id)
            db.getLikesDao().getLikes()
        }
    }

    private suspend fun fetchLikes(user_id: Int){
        val lastSavedDate = PrefUtils.with(context).getLastSavedAt(KEY_SAVED_AT)
        if (lastSavedDate == null || isFetchNeeded(context, KEY_SAVED_AT, MINUMUM_INTERVAL)){
            val response = apiRequest { api.getLikes(user_id) }
            likes.postValue(response)
        }
    }


    //suspend fun getLikes(comment_sahibi_id: Int) =  apiRequest { api.getLikes(comment_sahibi_id) }
}