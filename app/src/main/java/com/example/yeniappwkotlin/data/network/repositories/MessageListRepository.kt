package com.example.yeniappwkotlin.data.network.repositories

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.yeniappwkotlin.data.db.database.AppDatabase
import com.example.yeniappwkotlin.data.db.entities.MessageList
import com.example.yeniappwkotlin.data.network.MyApi
import com.example.yeniappwkotlin.data.network.SafeApiRequest
import com.example.yeniappwkotlin.util.Coroutines
import com.example.yeniappwkotlin.util.PrefUtils
import com.example.yeniappwkotlin.util.isFetchNeeded
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
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
            val response = apiRequest { api.getMessageList(userId) }
            messageList.postValue(response)
        }
    }

}