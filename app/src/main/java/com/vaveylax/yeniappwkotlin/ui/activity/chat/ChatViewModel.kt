package com.vaveylax.yeniappwkotlin.ui.activity.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import com.vaveylax.yeniappwkotlin.data.db.entities.MessageList
import com.vaveylax.yeniappwkotlin.data.network.repositories.ChatRepository
import com.vaveylax.yeniappwkotlin.ui.activity.comment.CommentListener
import com.vaveylax.yeniappwkotlin.util.ApiException
import com.vaveylax.yeniappwkotlin.util.Coroutines
import com.vaveylax.yeniappwkotlin.util.NoInternetException

class ChatViewModel(
    private val repository: ChatRepository
) : ViewModel() {
    var commentListener: CommentListener? = null

    fun saveChats(
        gonderen: String,
        alici: String,
        aliciName: String,
        message: String,
        photo: String,
        tarih: String
    ) {
        try {
            repository.saveChatMessage(gonderen, alici, aliciName, message, photo, tarih)
        } catch (e: ApiException) {
            commentListener?.onFailure(e.message!!)
        } catch (e: NoInternetException) {
            commentListener?.onFailure(e.message!!)
        } catch (e: Exception) {
            Log.d("EXCEPTION", e.message!!)
        }
    }

    suspend fun updateIsSeeMessage(
        user_id: Int, whoIsTalking: Int
    ) {
        try {
            repository.updateIsSeeMessage(user_id, whoIsTalking)
        } catch (e: ApiException) {
            commentListener?.onFailure(e.message!!)
        } catch (e: NoInternetException) {
            commentListener?.onFailure(e.message!!)
        } catch (e: Exception) {
            Log.d("EXCEPTION", e.message!!)
        }
    }

    fun saveMessageList(
        gonderen_id: Int,
        alici_id: Int,
        message: String,
        aliciNewMessage: Int,
        gonderenNewMessage: Int
    ) {
        Coroutines.main {
            try {
                val saveList = repository.saveMessageList(
                    gonderen_id,
                    alici_id,
                    message,
                    aliciNewMessage,
                    gonderenNewMessage
                )
                saveList.message.let {
                    commentListener?.onSuccess(it!!)
                }
            } catch (e: ApiException) {
                commentListener?.onFailure(e.message!!)
            } catch (e: NoInternetException) {
                commentListener?.onFailure(e.message!!)
            } catch (e: Exception) {
                Log.d("EXCEPTION", e.message!!)
            }
        }
    }

    fun updateMessageList(
        id: Int,
        userId: Int,
        currentUserId: Int,
        message: String,
        aliciNewCount: Int,
        gonderenNewCount: Int,
        is_alici: Int,
        is_gonderen: Int
    ) {
        Coroutines.main {
            try {
                val updateList = repository.updateMessageList(
                    id,
                    userId,
                    currentUserId,
                    message,
                    aliciNewCount,
                    gonderenNewCount,
                    is_alici,
                    is_gonderen
                )
                updateList.message.let {
                    commentListener?.onSuccess(it!!)
                }
            } catch (e: ApiException) {
                commentListener?.onFailure(e.message!!)
            } catch (e: NoInternetException) {
                commentListener?.onFailure(e.message!!)
            } catch (e: Exception) {
                Log.d("EXCEPTION", e.message!!)
            }
        }
    }

    fun pushNotification(
        user_name: String,
        other_user_name: String,
        commentName: String,
        durum: Int
    ) {
        Coroutines.main {
            try {
                repository.pushNotification(user_name, other_user_name, commentName, durum)
            } catch (e: ApiException) {
                commentListener?.onFailure(e.message!!)
            } catch (e: NoInternetException) {
                commentListener?.onFailure(e.message!!)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun getMessageList(userId: Int): List<MessageList> {
        val response: List<MessageList>?
        response = repository.getMessageList(userId)
        return response
    }

    fun updateLocalMessageList(
        tarih: String,
        message: String,
        id: Int,
        alici_new: Int,
        gonderen_new: Int
    ) {
        repository.updateLocalMessageList(tarih, message, id, alici_new, gonderen_new)
    }

    fun updateLocalMessageBadges(id: Int, alici_new: Int, gonderen_new: Int) {
        try {
            repository.updateLocalMessageBadges(id, alici_new, gonderen_new)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun saveLocalMessageList(messageList: MessageList) {
        repository.saveLocalMessageList(messageList)
    }
}