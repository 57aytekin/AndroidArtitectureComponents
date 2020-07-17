package com.vaveylax.yeniappwkotlin.ui.fragment.like

import android.view.View
import com.vaveylax.yeniappwkotlin.data.db.entities.Likes

interface ClickListener {
    fun onRecyclerViewItemClick(view : View, likes: Likes)
}