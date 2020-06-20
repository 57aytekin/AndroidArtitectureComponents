package com.example.yeniappwkotlin.ui.activity.auth

import androidx.lifecycle.ViewModel
import com.example.yeniappwkotlin.data.db.entities.User
import com.example.yeniappwkotlin.data.network.repositories.UserRepository

class AuthViewModel(
    private val repository: UserRepository
) : ViewModel() {

    fun getLoggedInUser() = repository.getUser()

    suspend fun userLogin(
        email : String, password : String
    ) = repository.userLogin(email, password)

    suspend fun userRegister(
        name: String, email : String, password : String, paths : String, is_social_account : Int
    ) = repository.userRegister(name, email, password, paths, is_social_account)

    suspend fun saveLoggedInUser(user: User) = repository.saveUser(user)
}