package com.example.yeniappwkotlin.ui.fragment.profile

import androidx.lifecycle.ViewModel
import com.example.yeniappwkotlin.data.db.database.AppDatabase
import com.example.yeniappwkotlin.data.network.MyApi
import com.example.yeniappwkotlin.data.network.repositories.UserRepository

class ProfileViewModel(
    private val repository: UserRepository
) : ViewModel() {
    suspend fun deleteUser() = repository.deleteUser()
}
