package com.example.yeniappwkotlin.ui.fragment.profile_paylasimlar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.yeniappwkotlin.data.db.entities.Post
import com.example.yeniappwkotlin.data.network.repositories.PostRepository
import com.example.yeniappwkotlin.data.network.repositories.UserRepository
import com.example.yeniappwkotlin.ui.activity.comment.CommentListener
import com.example.yeniappwkotlin.util.Coroutines
import kotlinx.coroutines.Job

class ProfilePaylasimlarViewModel(
    val repository: PostRepository,
    val user_id : Int
) : ViewModel() {
    private lateinit var job : Job
    var commentListener : CommentListener? = null

    private val _posts = MutableLiveData<List<Post>>()
    val posts : LiveData<List<Post>>
    get() = _posts

    /*fun getPosts(){
        job= Coroutines.ioThenMain(
            { repository.getPosts(user_id)},
            { _posts.value = it }
        )
    }*/
}
