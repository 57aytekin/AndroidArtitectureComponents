package com.example.yeniappwkotlin.ui.activity.edit_profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.yeniappwkotlin.data.network.repositories.UserRepository

@Suppress("UNCHECKED_CAST")
class EditProfileFactory(
    private val repository: UserRepository
): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EditProfileViewModel(repository) as T
    }
}