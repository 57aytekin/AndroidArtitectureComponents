package com.vaveylax.yeniappwkotlin.ui.fragment.message

import androidx.lifecycle.ViewModel
import com.vaveylax.yeniappwkotlin.data.network.repositories.MessageListRepository
import com.vaveylax.yeniappwkotlin.util.lazyDeffered

class MessageViewModel(
    private val repository: MessageListRepository,
    private val userId : Int
) : ViewModel() {

    val getMessageList by lazyDeffered {
        repository.getMessageList(userId)
    }
}
