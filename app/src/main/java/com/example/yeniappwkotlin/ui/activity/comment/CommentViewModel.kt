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
    private val repository: CommentRepository,
    private val user_id : Int?,
    private val post_id : Int?
) : ViewModel() {
    private lateinit var job : Job
    var comment: String? = null
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

    fun onSaveCommentClick(view : View){
        commentListener?.onStarted()
        if(comment.isNullOrEmpty()){
            commentListener?.onFailure("Lütfen paylaşımınızı giriniz.")
            return
        }
        Coroutines.main {
            try {
                val commentResponse = repository.saveComments(user_id!!, post_id!!, comment!!,0)
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