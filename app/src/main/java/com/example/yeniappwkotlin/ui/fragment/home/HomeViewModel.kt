package com.example.yeniappwkotlin.ui.fragment.home

import android.content.Context
import android.util.Log
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
import com.example.yeniappwkotlin.util.lazyDeffered
import kotlinx.coroutines.*
import java.lang.Exception

class HomeViewModel(
    private val repository: PostRepository,
    private val user_id: Int,
    private val page : Int,
    private val row_per_page : Int
) : ViewModel() {

    private lateinit var job : Job
    var commentListener : CommentListener? = null

    val getPost by lazyDeffered {
        repository.getPosts(user_id, page, row_per_page)
    }

    fun savePost(post : List<Post>){
        repository.savePost(post)
    }

    fun saveUserPostLikes(user_id: Int, post_id: Int, begeniDurum: Int, like_count: Int, post_sahibi_id: Int) {
        Coroutines.main {
            try {
                val userPostLikes = repository.saveUserPostLikes(user_id, post_id, begeniDurum, like_count, post_sahibi_id)
                userPostLikes.message.let {
                    repository.updateLocalPostCount(like_count, begeniDurum, post_id)
                    commentListener?.onSuccess(it)
                    return@main
                }
            } catch (e: ApiException) {
                commentListener?.onFailure(e.message!!)
            } catch (e: NoInternetException) {
                commentListener?.onFailure(e.message!!)
            } catch (e : Exception){
                commentListener?.onFailure(e.message!!)
            }
        }
    }


    fun btnPostLike(post_id: Int, user_id: Int, like_count: Int, begeniDurum: Int) {
        val newLikeCount: Int = if (begeniDurum == 1){
            like_count+1
        }else{
            like_count -1
        }
        commentListener?.onStarted()
        Coroutines.main {
            try {
                val likeResponse =
                    repository.updateLikeCounts(post_id, user_id, newLikeCount, begeniDurum)
                likeResponse.message.let {
                    repository.updateLocalPostCount(newLikeCount, begeniDurum, post_id)
                    commentListener?.onSuccess(it!!)
                    return@main
                }
            } catch (e: ApiException) {
                commentListener?.onFailure(e.message!!)
            } catch (e: NoInternetException) {
                commentListener?.onFailure(e.message!!)
            } catch (e : Exception){
                commentListener?.onFailure(e.message!!)
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

    override fun onCleared() {
        super.onCleared()
        if (::job.isInitialized) job.cancel()
    }
}
