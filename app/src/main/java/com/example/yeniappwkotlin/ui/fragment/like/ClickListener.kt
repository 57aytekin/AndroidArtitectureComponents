package com.example.yeniappwkotlin.ui.fragment.like

import android.view.View
import com.example.yeniappwkotlin.data.db.entities.Likes

interface ClickListener {
    fun onRecyclerViewItemClick(view : View, likes: Likes)
}