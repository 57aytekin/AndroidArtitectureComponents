package com.example.yeniappwkotlin.ui.fragment.home

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.yeniappwkotlin.data.db.entities.Post
import com.example.yeniappwkotlin.data.network.repositories.PostRepository
import com.example.yeniappwkotlin.ui.activity.comment.CommentListener
import com.example.yeniappwkotlin.util.ApiException
import com.example.yeniappwkotlin.util.Coroutines
import com.example.yeniappwkotlin.util.NoInternetException
import kotlinx.coroutines.Job

class HomeViewModel(
    private val repository: PostRepository
) : ViewModel() {

    private lateinit var job : Job
    var commentListener : CommentListener? = null

    private val _posts = MutableLiveData<List<Post>>()
    val posts : LiveData<List<Post>>
    get() = _posts

    fun getPosts(){
        job= Coroutines.ioThenMain(
            { repository.getPosts()},
            { _posts.value = it }
        )
    }

    fun btnPostLike(like_count : Int, post_id: Int){
        val likeCount = like_count+1
        commentListener?.onStarted()
        Coroutines.main {
            try {
                val likeResponse = repository.updateLikeCounts(likeCount, post_id)
                likeResponse.message.let {
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
