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
        userName : String, firsName : String,lastName : String, email: String, phone: String, password: String, paths: String, is_social_account : Int
    ) = repository.userRegister(userName, firsName,lastName, email, phone, password, paths, is_social_account)

    suspend fun saveLoggedInUser(user: User) = repository.saveUser(user)
}