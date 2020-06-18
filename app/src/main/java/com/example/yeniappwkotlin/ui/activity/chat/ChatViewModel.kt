package com.example.yeniappwkotlin.ui.activity.chat

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

    fun saveMessageList(gonderen_id: Int, alici_id: Int, alici_name : String, alici_photo : String, message : String){
        Coroutines.main {
            try {
                val saveList = repository.saveMessageList(gonderen_id, alici_id, alici_name, alici_photo, message)
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
    fun updateMessageList(id: Int, message : String){
        Coroutines.main {
            try {
                val updateList = repository.updateMessageList(id, message)
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

    fun updateLocalMessageList(tarih : String, message : String, id : Int){
        repository.updateLocalMessageList(tarih, message, id)
    }
}