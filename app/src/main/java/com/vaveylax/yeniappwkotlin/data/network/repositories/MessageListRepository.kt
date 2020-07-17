package com.vaveylax.yeniappwkotlin.data.network.repositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.vaveylax.yeniappwkotlin.R
import com.vaveylax.yeniappwkotlin.data.db.database.AppDatabase
import com.vaveylax.yeniappwkotlin.data.db.entities.MessageList
import com.vaveylax.yeniappwkotlin.data.network.MyApi
import com.vaveylax.yeniappwkotlin.data.network.SafeApiRequest
import com.vaveylax.yeniappwkotlin.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class MessageListRepository(
    private val api : MyApi,
    private val db : AppDatabase,
    private val context : Context
) : SafeApiRequest(){
    private val messageList = MutableLiveData<List<MessageList>>()
    private val MINUMUM_INTERVAL = -1
    private val KEY_SAVED_AT = "message_list_key_saved_at"
    private var lastDate = SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.ENGLISH).format(Date())

    init {
        messageList.observeForever {
            saveMessageList(it)
        }
    }

    private fun saveMessageList(messageList : List<MessageList>){
        Coroutines.io {
            PrefUtils.with(context).saveLastSavedAt(KEY_SAVED_AT, lastDate)
            db.getMessageListDao().saveAllMessage(messageList)
        }
    }

    suspend fun getMessageList(userId: Int) : LiveData<List<MessageList>>{
        return withContext(Dispatchers.IO){
            fetchMessageList(userId)
            db.getMessageListDao().getMessageList()
        }
    }
    private suspend fun fetchMessageList(userId : Int){
        val lastSavedAt = PrefUtils.with(context).getLastSavedAt(KEY_SAVED_AT)
        if (lastSavedAt == null || isFetchNeeded(context, KEY_SAVED_AT, MINUMUM_INTERVAL)){
            try {
                val response = apiRequest { api.getMessageList(userId) }
                db.getUserDao().deleteMessageList()
                messageList.postValue(response)
            } catch (e: ApiException) {
                Log.d("MESSAGE_1",e.message!!)
            } catch (e: NoInternetException) {
                Coroutines.main { context.toast(context.getString(R.string.check_internet)) }
                messageList.postValue(db.getMessageListDao().getLocalMessageList())
            } catch (e : Exception){
                Log.d("MESSAGE_3",e.message!!)
            }
        }
    }

}