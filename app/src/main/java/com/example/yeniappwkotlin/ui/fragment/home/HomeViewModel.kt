package com.example.yeniappwkotlin.ui.fragment.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.yeniappwkotlin.data.db.entities.Post
import com.example.yeniappwkotlin.data.network.repositories.PostRepository
import com.example.yeniappwkotlin.util.Coroutines
import kotlinx.coroutines.Job

class HomeViewModel(
    private val repository: PostRepository
) : ViewModel() {

    private lateinit var job : Job

    private val _posts = MutableLiveData<List<Post>>()
    val posts : LiveData<List<Post>>
    get() = _posts

    fun getPosts(){
        job= Coroutines.ioThenMain(
            { repository.getPosts()},
            { _posts.value = it?.posts }
        )
    }

    override fun onCleared() {
        super.onCleared()
        if (::job.isInitialized) job.cancel()
    }
}
