package com.example.yeniappwkotlin.ui.fragment.profile_paylasimlar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.yeniappwkotlin.data.network.repositories.PostRepository

@Suppress("UNCHECKED_CAST")
class ProfilePaylasimlarFactory(
    val repository : PostRepository,
    val user_id : Int
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ProfilePaylasimlarViewModel(repository, user_id) as T
    }
}