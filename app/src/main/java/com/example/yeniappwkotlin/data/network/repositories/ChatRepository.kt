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

    fun saveChatMessage(gonderen : String, alici : String, aliciName :String, message: String, photo : String, tarih : String) {
        val reference : DatabaseReference = FirebaseDatabase.getInstance().reference
        val chatMap = HashMap<String, String>()
        chatMap["gonderen"] = gonderen
        chatMap["alici"] = alici
        chatMap["aliciName"] = aliciName
        chatMap["message"] = message
        chatMap["photo"] = photo
        chatMap["tarih"] = tarih
        reference.child("Chats").push().setValue(chatMap)
    }

    suspend fun saveMessageList(gonderen_id: Int, alici_id: Int, alici_name : String, alici_photo : String, message : String) : CommentResponse{
        return apiRequest { api.saveMessageList(gonderen_id, alici_id, alici_name, alici_photo, message) }
    }

    suspend fun updateMessageList(id : Int, message : String) : CommentResponse{
        return apiRequest { api.updateMessageList(id, message) }
    }

    fun updateLocalMessageList(tarih : String, message : String, id : Int){
        CoroutineScope(Dispatchers.IO).launch {
            db.getMessageListDao().updateLocalMessageList(tarih, message, id)
        }
    }

}