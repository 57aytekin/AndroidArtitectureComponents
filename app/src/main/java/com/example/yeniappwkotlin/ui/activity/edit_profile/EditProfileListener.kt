package com.example.yeniappwkotlin.ui.activity.edit_profile

interface EditProfileListener {
    fun onStarted()
    fun onSuccess(message : String)
    fun onFailure(message: String)
}