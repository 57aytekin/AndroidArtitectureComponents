package com.vaveylax.yeniappwkotlin.ui.fragment.message

import android.view.View
import com.vaveylax.yeniappwkotlin.data.db.entities.MessageList

interface MessageClickListener {
    fun onRecyclerViewItemClick(view : View, message: MessageList)
}