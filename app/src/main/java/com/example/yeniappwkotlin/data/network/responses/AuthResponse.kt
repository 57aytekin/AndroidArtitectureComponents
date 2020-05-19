package com.example.yeniappwkotlin.data.network.responses

import com.example.yeniappwkotlin.data.db.entities.User

data class AuthResponse(
    val isSuccessful : Boolean?,
    val message : String?,
    val login : User?
)