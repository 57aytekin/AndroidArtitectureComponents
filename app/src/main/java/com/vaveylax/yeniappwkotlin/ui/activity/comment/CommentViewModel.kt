package com.vaveylax.yeniappwkotlin.ui.activity.comment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.vaveylax.yeniappwkotlin.data.db.entities.Comment
import com.vaveylax.yeniappwkotlin.data.network.repositories.CommentRepository
import com.vaveylax.yeniappwkotlin.util.ApiException
import com.vaveylax.yeniappwkotlin.util.Coroutines
import com.vaveylax.yeniappwkotlin.util.NoInternetException
import kotlinx.coroutines.Job

class CommentViewModel(
    private val repository: CommentRepository
) : ViewModel() {
    private lateinit var job : Job
    var commentListener : CommentListener? = null

    private val _comment = MutableLiveData<List<Comment>>()
    val comments: LiveData<List<Comment>>
    get() = _comment

    fun getComments(){
        try {
            job = Coroutines.ioThenMain(
                {repository.getComments()},
                {_comment.value = it}
            )
        }catch (e: ApiException){
            commentListener?.onFailure(e.message!!)
        }catch (e : NoInternetException){
            commentListener?.onFailure("check_internet")
        }catch (e : Exception){
            commentListener?.onFailure(e.message!!)
        }
    }

    fun saveLikes(post_sahibi_id : Int, comment_sahibi_id: Int, comment_id: Int, post_id: Int){
        Coroutines.main {
            try {
                val saveLikes = repository.saveLikes(post_sahibi_id, comment_sahibi_id, comment_id, post_id)
                saveLikes.message.let {
                    commentListener?.onSuccess(it!!)
                    return@main
                }
            }catch (e: ApiException){
                commentListener?.onFailure(e.message!!)
            }catch (e : NoInternetException){
                commentListener?.onFailure(e.message!!)
            }
        }
    }

    fun pushNotification(user_name: String, other_user_name: String, commentName: String, durum: Int){
        Coroutines.main {
            try {
                repository.pushNotification(user_name, other_user_name, commentName, durum)
            }catch (e : ApiException){
                e.printStackTrace()
            }catch (e : NoInternetException){
                e.printStackTrace()
            }catch (e : Exception){
                e.printStackTrace()
            }
        }
    }

    suspend fun updateCommentLike(comment_id : Int, begeniDurum : Int) =
        repository.updateCommentLike(comment_id, begeniDurum)

    fun onSaveCommentClick(user_id : Int, post_id: Int,comment : String){
        commentListener?.onStarted()
        Coroutines.main {
            try {
                val commentResponse = repository.saveComments(user_id, post_id, comment,0)
                commentResponse.message.let {
                    commentListener?.onSuccess(it!!)
                    return@main
                }
            }catch (e : ApiException){
                commentListener?.onFailure(e.message!!)
            }catch (e : NoInternetException){
                commentListener?.onFailure(e.message!!)
            }catch (e : Exception){
                e.printStackTrace()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (::job.isInitialized) job.cancel()
    }
}