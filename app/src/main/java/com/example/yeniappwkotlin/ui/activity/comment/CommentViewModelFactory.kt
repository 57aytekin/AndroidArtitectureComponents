package com.example.yeniappwkotlin.ui.activity.comment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.yeniappwkotlin.data.network.repositories.CommentRepository

@Suppress("UNCHECKED_CAST")
class CommentViewModelFactory(
    private val repository: CommentRepository,
    private val user_id : Int?,
    private val post_id : Int?
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CommentViewModel(repository, user_id, post_id) as T
    }
}