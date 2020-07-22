package com.vaveylax.yeniappwkotlin.ui.activity.comment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vaveylax.yeniappwkotlin.data.network.repositories.CommentRepository

@Suppress("UNCHECKED_CAST")
class CommentViewModelFactory(
    private val repository: CommentRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return CommentViewModel(repository) as T
    }
}