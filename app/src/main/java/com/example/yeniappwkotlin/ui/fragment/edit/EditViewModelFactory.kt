package com.example.yeniappwkotlin.ui.fragment.edit

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.yeniappwkotlin.data.db.entities.User
import com.example.yeniappwkotlin.data.network.repositories.UserRepository
import com.google.android.material.bottomnavigation.BottomNavigationView

@Suppress("UNCHECKED_CAST")
class EditViewModelFactory(
    private val activity :Activity,
    private val repository: UserRepository,
    private val userId : Int,
    private val bottomNavigation : BottomNavigationView
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return EditViewModel(activity,repository, userId, bottomNavigation) as T
    }
}