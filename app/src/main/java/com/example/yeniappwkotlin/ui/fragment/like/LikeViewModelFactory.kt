package com.example.yeniappwkotlin.ui.fragment.like

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.yeniappwkotlin.data.network.repositories.LikesRepository

@Suppress("UNCHECKED_CAST")
class LikeViewModelFactory(
    private val repository: LikesRepository,
    private val userId : Int
) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return LikeViewModel(repository, userId) as T
    }
}