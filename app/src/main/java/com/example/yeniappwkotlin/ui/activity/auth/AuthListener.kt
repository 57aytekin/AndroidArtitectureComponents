package com.example.yeniappwkotlin.ui.activity.auth

import com.example.yeniappwkotlin.data.db.entities.User

interface AuthListener {
    fun onStarted()
    fun onSuccess(user : User)
    fun onFailure(message : String)
}