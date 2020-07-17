package com.vaveylax.yeniappwkotlin.ui.activity.auth

import com.vaveylax.yeniappwkotlin.data.db.entities.User

interface AuthListener {
    fun onStarted()
    fun onSuccess(user : User)
    fun onFailure(message : String)
}