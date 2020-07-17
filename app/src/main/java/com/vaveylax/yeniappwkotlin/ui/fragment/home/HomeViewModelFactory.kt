package com.vaveylax.yeniappwkotlin.ui.fragment.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vaveylax.yeniappwkotlin.data.network.repositories.PostRepository

@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory(
    private val repository: PostRepository,
    private val user_id : Int,
    private val page : Int,
    private val row_per_page : Int
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return HomeViewModel(repository, user_id, page, row_per_page) as T
    }
}