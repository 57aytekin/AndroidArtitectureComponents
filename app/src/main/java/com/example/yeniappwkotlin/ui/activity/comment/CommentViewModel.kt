package com.example.yeniappwkotlin.ui.activity.comment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.yeniappwkotlin.data.db.entities.Comment
import com.example.yeniappwkotlin.data.network.repositories.CommentRepository
import com.example.yeniappwkotlin.util.Coroutines
import kotlinx.coroutines.Job

class CommentViewModel(
    private val repository: CommentRepository
) : ViewModel() {
    private lateinit var job : Job

    private val _comment = MutableLiveData<List<Comment>>()
    val comments: LiveData<List<Comment>>
    get() = _comment

    fun getComments(){
        job = Coroutines.ioThenMain(
            {repository.getComments()},
            {_comment.value = it}
        )
    }

    override fun onCleared() {
        super.onCleared()
        if (::job.isInitialized) job.cancel()
    }
}