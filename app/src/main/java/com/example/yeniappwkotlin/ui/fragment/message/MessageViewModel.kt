package com.example.yeniappwkotlin.ui.fragment.message

import androidx.lifecycle.ViewModel
import com.example.yeniappwkotlin.data.db.entities.MessageList
import com.example.yeniappwkotlin.data.network.repositories.MessageListRepository
import com.example.yeniappwkotlin.util.lazyDeffered

class MessageViewModel(
    private val repository: MessageListRepository,
    private val userId : Int
) : ViewModel() {

    val getMessageList by lazyDeffered {
        repository.getMessageList(userId)
    }
}
