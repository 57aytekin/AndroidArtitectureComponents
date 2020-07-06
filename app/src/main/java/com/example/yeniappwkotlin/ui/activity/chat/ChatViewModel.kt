package com.example.yeniappwkotlin.ui.activity.chat

import android.os.Message
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.yeniappwkotlin.data.db.entities.Chat
import com.example.yeniappwkotlin.data.db.entities.MessageList
import com.example.yeniappwkotlin.data.network.repositories.ChatRepository
import com.example.yeniappwkotlin.ui.activity.comment.CommentListener
import com.example.yeniappwkotlin.util.ApiException
import com.example.yeniappwkotlin.util.Coroutines
import com.example.yeniappwkotlin.util.NoInternetException
import com.example.yeniappwkotlin.util.lazyDeffered
import com.google.firebase.database.DatabaseReference

class ChatViewModel(
    private val repository: ChatRepository
) : ViewModel() {
    var commentListener : CommentListener? = null

    fun saveChats(gonderen : String, alici : String, aliciName :String, message: String, photo : String, tarih : String){
        repository.saveChatMessage(gonderen, alici, aliciName, message, photo, tarih)
    }

    suspend fun updateIsSeeMessage(
        user_id : Int, whoIsTalking : Int
    ) = repository.updateIsSeeMessage(user_id, whoIsTalking)

    fun saveMessageList(gonderen_id: Int, alici_id: Int, message : String, aliciNewMessage : Int, gonderenNewMessage : Int){
        Coroutines.main {
            try {
                val saveList = repository.saveMessageList(gonderen_id, alici_id, message, aliciNewMessage, gonderenNewMessage)
                saveList.message.let {
                    commentListener?.onSuccess(it!!)
                }
            }catch (e : ApiException){
                commentListener?.onFailure(e.message!!)
            }catch (e : NoInternetException){
                commentListener?.onFailure(e.message!!)
            }catch (e : Exception){
                Log.d("EXCEPTION", e.message!!)
            }
        }
    }
    fun updateMessageList(id: Int, userId : Int, currentUserId : Int, message : String, aliciNewCount : Int, gonderenNewCount : Int, is_alici : Int, is_gonderen : Int){
        Coroutines.main {
            try {
                val updateList = repository.updateMessageList(id, userId, currentUserId, message, aliciNewCount, gonderenNewCount, is_alici, is_gonderen)
                updateList.message.let {
                    commentListener?.onSuccess(it!!)
                }
            }catch (e : ApiException){
                commentListener?.onFailure(e.message!!)
            }catch (e : NoInternetException){
                commentListener?.onFailure(e.message!!)
            }catch (e : Exception){
                Log.d("EXCEPTION", e.message!!)
            }
        }
    }

    fun pushNotification(user_name: String, other_user_name: String, commentName: String, durum: Int){
        Coroutines.main {
            try {
                repository.pushNotification(user_name, other_user_name,  commentName, durum)
            }catch (e : ApiException){
                commentListener?.onFailure(e.message!!)
            }catch (e : NoInternetException){
                commentListener?.onFailure(e.message!!)
            }catch (e : Exception){
                e.printStackTrace()
            }
        }
    }

    suspend fun getMessageList(userId : Int) : List<MessageList>{
        val response : List<MessageList>?
        response = repository.getMessageList(userId)
        return response
    }

    fun updateLocalMessageList(tarih : String, message : String, id : Int, alici_new : Int, gonderen_new: Int){
        repository.updateLocalMessageList(tarih, message, id, alici_new, gonderen_new)
    }
    fun updateLocalMessageBadges(id : Int, alici_new : Int, gonderen_new: Int){
        repository.updateLocalMessageBadges(id, alici_new, gonderen_new)
    }
    fun saveLocalMessageList(messageList: MessageList){
        repository.saveLocalMessageList(messageList)
    }
}