package com.example.yeniappwkotlin.ui.activity.comment

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.yeniappwkotlin.data.db.entities.Comment
import com.example.yeniappwkotlin.data.network.repositories.CommentRepository
import com.example.yeniappwkotlin.util.ApiException
import com.example.yeniappwkotlin.util.Coroutines
import com.example.yeniappwkotlin.util.NoInternetException
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
        job = Coroutines.ioThenMain(
            {repository.getComments()},
            {_comment.value = it}
        )
    }

    fun saveLikes(post_sahibi_id : Int, comment_sahibi_id: Int, comment_id: Int){
        Coroutines.main {
            try {
                val saveLikes = repository.saveLikes(post_sahibi_id, comment_sahibi_id, comment_id)
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
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (::job.isInitialized) job.cancel()
    }
}