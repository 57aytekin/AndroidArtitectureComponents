package com.example.yeniappwkotlin.ui.fragment.message

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.yeniappwkotlin.data.network.repositories.MessageListRepository

@Suppress("UNCHECKED_CAST")
class MessageViewModelFactory(
    private val repository: MessageListRepository,
    private val userId : Int
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MessageViewModel(repository, userId) as T
    }
}