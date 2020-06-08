package com.example.yeniappwkotlin.ui.fragment.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.yeniappwkotlin.data.network.repositories.PostRepository

@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory(
    private val repository: PostRepository,
    private val user_id : Int
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(repository, user_id) as T
    }
}