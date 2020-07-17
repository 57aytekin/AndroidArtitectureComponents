package com.vaveylax.yeniappwkotlin.ui.activity.edit_profile

interface EditProfileListener {
    fun onStarted()
    fun onSuccess(message : String)
    fun onFailure(message: String)
}