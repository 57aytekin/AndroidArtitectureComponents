package com.vaveylax.yeniappwkotlin.ui.fragment.edit

interface EditListener {
    fun onStarted()
    fun onSuccess(message : String)
    fun onFailure(message: String)
}