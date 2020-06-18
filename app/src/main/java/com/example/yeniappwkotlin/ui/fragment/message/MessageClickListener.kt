package com.example.yeniappwkotlin.ui.fragment.message

import android.view.View
import com.example.yeniappwkotlin.data.db.entities.MessageList

interface MessageClickListener {
    fun onRecyclerViewItemClick(view : View, message: MessageList)
}