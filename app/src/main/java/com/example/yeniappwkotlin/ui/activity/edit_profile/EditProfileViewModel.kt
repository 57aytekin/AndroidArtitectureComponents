package com.example.yeniappwkotlin.ui.activity.edit_profile

import androidx.lifecycle.ViewModel
import com.example.yeniappwkotlin.data.db.entities.User
import com.example.yeniappwkotlin.data.network.repositories.UserRepository

class EditProfileViewModel(
    private val repository: UserRepository
) : ViewModel() {
    var editProfileListener: EditProfileListener? = null

    suspend fun updateUserProfile(
        user_id: Int,
        user_name: String,
        user_first_name: String,
        user_last_name: String,
        image: String
    ) = repository.updateUserProfile(user_id, user_name, user_first_name, user_last_name, image)

    suspend fun updateLocalUser(
        userName: String,
        firstName: String,
        lastName: String,
        paths: String,
        userId: Int
    ) = repository.updateLocalUser(userName, firstName, lastName, paths, userId)
}