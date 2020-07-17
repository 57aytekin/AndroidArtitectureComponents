package com.vaveylax.yeniappwkotlin.ui.activity.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.vaveylax.yeniappwkotlin.data.network.repositories.ChatRepository

@Suppress("UNCHECKED_CAST")
class ChatViewModelFactory(
    private val repository: ChatRepository
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ChatViewModel(repository) as T
    }
}