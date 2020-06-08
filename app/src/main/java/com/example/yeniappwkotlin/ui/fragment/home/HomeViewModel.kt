package com.example.yeniappwkotlin.ui.fragment.home

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.yeniappwkotlin.data.db.entities.Post
import com.example.yeniappwkotlin.data.db.entities.PostLikes
import com.example.yeniappwkotlin.data.network.repositories.PostRepository
import com.example.yeniappwkotlin.data.network.responses.PostLikesResponse
import com.example.yeniappwkotlin.ui.activity.comment.CommentListener
import com.example.yeniappwkotlin.util.ApiException
import com.example.yeniappwkotlin.util.Coroutines
import com.example.yeniappwkotlin.util.NoInternetException
import kotlinx.coroutines.Job

class HomeViewModel(
    private val repository: PostRepository,
    private val user_id: Int
) : ViewModel() {

    private lateinit var job : Job
    var commentListener : CommentListener? = null

    private val _posts = MutableLiveData<List<Post>>()
    val posts : LiveData<List<Post>>
    get() = _posts

    private val _userLikes = MutableLiveData<PostLikesResponse>()
    val likes : LiveData<PostLikesResponse>
    get() = _userLikes

    fun getLikes(){
        job = Coroutines.ioThenMain(
            {repository.getUserPostLikes(user_id)},
            {_userLikes.value = it}
        )
    }

    fun getPosts(){
        job= Coroutines.ioThenMain(
            { repository.getPosts(user_id)},
            { _posts.value = it }
        )
    }

    fun saveUserPostLikes( user_id: Int, post_id: Int, begeniDurum : Int){
        Coroutines.main {
            try {
                val userPostLikes = repository.saveUserPostLikes(user_id, post_id, begeniDurum)
                userPostLikes.message.let {
                    commentListener?.onSuccess(it)
                    return@main
                }
            }catch (e : ApiException){
                commentListener?.onFailure(e.message!!)
            }catch (e : NoInternetException){
                commentListener?.onFailure(e.message!!)
            }
        }
    }

    fun btnPostLike(post_id: Int, user_id: Int, like_count : Int, begeniDurum: Int){
        val likeCount = like_count+1
        commentListener?.onStarted()
        Coroutines.main {
            try {
                val likeResponse = repository.updateLikeCounts(post_id, user_id, likeCount, begeniDurum)
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
