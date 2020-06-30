package com.example.yeniappwkotlin.data.network.repositories

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.yeniappwkotlin.data.db.database.AppDatabase
import com.example.yeniappwkotlin.data.db.entities.Chat
import com.example.yeniappwkotlin.data.db.entities.MessageList
import com.example.yeniappwkotlin.data.db.entities.Post
import com.example.yeniappwkotlin.data.network.MyApi
import com.example.yeniappwkotlin.data.network.SafeApiRequest
import com.example.yeniappwkotlin.data.network.responses.CommentResponse
import com.google.firebase.database.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.http.Field

class ChatRepository(
    val api :MyApi,
    val db : AppDatabase,
    val context : Context
): SafeApiRequest() {
    val chatMap = HashMap<String, String>()

    fun saveChatMessage(gonderen : String, alici : String, aliciName :String, message: String, photo : String, tarih : String) {
        val reference : DatabaseReference = FirebaseDatabase.getInstance().reference
        chatMap.clear()
        chatMap["gonderen"] = gonderen
        chatMap["alici"] = alici
        chatMap["aliciName"] = aliciName
        chatMap["message"] = message
        chatMap["photo"] = photo
        chatMap["tarih"] = tarih
        reference.child("Chats").push().setValue(chatMap)
    }

    suspend fun saveMessageList(gonderen_id: Int, alici_id: Int, message : String, aliciNewMessage : Int, gonderenNewMessage : Int) : CommentResponse{
        return apiRequest { api.saveMessageList(gonderen_id, alici_id, message, aliciNewMessage, gonderenNewMessage) }
    }

    suspend fun updateIsSeeMessage(user_id: Int, is_login: Int): CommentResponse{
        return apiRequest { api.updateIsLogin(user_id, is_login) }
    }

    suspend fun updateMessageList(id : Int, userId : Int, message : String, aliciNewCount : Int, gonderenNewCount : Int, is_alici : Int, is_gonderen : Int) : CommentResponse{
        return apiRequest { api.updateMessageList(id, userId, message, aliciNewCount, gonderenNewCount, is_alici, is_gonderen) }
    }

    suspend fun getMessageList(userId : Int) : List<MessageList>{
        return apiRequest { api.getMessageList(userId) }
    }

    fun updateLocalMessageList(tarih : String, message : String, id : Int, alici_new : Int, gonderen_new: Int){
        CoroutineScope(Dispatchers.IO).launch {
            db.getMessageListDao().updateLocalMessageList(tarih, message, id, alici_new, gonderen_new)
        }
    }
    fun updateLocalMessageBadges(id : Int, alici_new : Int, gonderen_new: Int){
        CoroutineScope(Dispatchers.IO).launch {
            db.getMessageListDao().updateLocalBadges(id, alici_new, gonderen_new)
        }
    }
    fun saveLocalMessageList( messageList: MessageList){
        CoroutineScope(Dispatchers.IO).launch {
            db.getMessageListDao().saveLocalMessageList(messageList)
        }
    }



}