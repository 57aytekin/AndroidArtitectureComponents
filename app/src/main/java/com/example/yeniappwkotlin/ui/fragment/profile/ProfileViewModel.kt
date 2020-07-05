package com.example.yeniappwkotlin.ui.fragment.profile

import androidx.lifecycle.ViewModel
import com.example.yeniappwkotlin.data.db.database.AppDatabase
import com.example.yeniappwkotlin.data.network.MyApi
import com.example.yeniappwkotlin.data.network.repositories.UserRepository

class ProfileViewModel(
    private val repository: UserRepository
) : ViewModel() {
    suspend fun updateIsLogin(
        user_id : Int, is_login : Int
    ) = repository.updateIsLogin(user_id, is_login)

    suspend fun updateUserLoginStatu(
        user_id : Int, is_login : Int
    ) = repository.updateUserLoginStatu(user_id, is_login)

    fun deleteUser() = repository.deleteUser()
    fun deletePost() = repository.deletePost()
    fun deleteLikes() = repository.deleteLikes()
    fun deleteMessageList() = repository.deleteMessageList()
}
