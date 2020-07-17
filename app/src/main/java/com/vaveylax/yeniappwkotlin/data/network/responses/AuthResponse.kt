package com.vaveylax.yeniappwkotlin.data.network.responses

import com.vaveylax.yeniappwkotlin.data.db.entities.User

data class AuthResponse(
    val isSuccessful : Boolean?,
    val message : String?,
    val login : User?
)