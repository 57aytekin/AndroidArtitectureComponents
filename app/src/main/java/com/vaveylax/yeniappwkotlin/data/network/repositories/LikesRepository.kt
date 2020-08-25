package com.vaveylax.yeniappwkotlin.data.network.repositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vaveylax.yeniappwkotlin.R
import com.vaveylax.yeniappwkotlin.data.db.database.AppDatabase
import com.vaveylax.yeniappwkotlin.data.db.entities.Likes
import com.vaveylax.yeniappwkotlin.data.network.MyApi
import com.vaveylax.yeniappwkotlin.data.network.SafeApiRequest
import com.vaveylax.yeniappwkotlin.util.*
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
    private val MINUMUM_INTERVAL = 5
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
            try {
                val response = apiRequest { api.getLikes(user_id) }
                likes.postValue(response)
            } catch (e: ApiException) {
                Log.d("LIKES_1",e.message!!)
            } catch (e: NoInternetException) {
                likes.postValue(db.getLikesDao().getLocalLikes())
                Coroutines.main { context.toast(context.getString(R.string.check_internet)) }
            } catch (e : java.lang.Exception){
                Log.d("LIKES_3",e.message!!)
            }

        }
    }


    suspend fun updateLikesBudgetCount(userId: Int) =  apiRequest { api.updateLikesBudgetCount(userId) }
}