package com.vaveylax.yeniappwkotlin.ui.fragment.edit

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vaveylax.yeniappwkotlin.data.network.repositories.UserRepository

@Suppress("UNCHECKED_CAST")
class EditViewModelFactory(
    private val activity :Activity,
    private val repository: UserRepository,
    private val userId : Int
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EditViewModel(activity,repository, userId) as T
    }
}