package com.example.yeniappwkotlin.ui.fragment.home

import android.view.View
import com.example.yeniappwkotlin.data.db.entities.Post

interface RecyclerViewClickListener {
    fun onRecyclerViewItemClick(view : View, post : Post)
}