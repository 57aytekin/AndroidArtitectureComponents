package com.vaveylax.yeniappwkotlin.ui.activity.comment

interface CommentListener {
    fun onStarted()
    fun onSuccess(message : String)
    fun onFailure(message: String)
}