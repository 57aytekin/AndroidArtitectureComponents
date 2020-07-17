package com.vaveylax.yeniappwkotlin.ui.fragment.home

import android.view.View
import com.vaveylax.yeniappwkotlin.data.db.entities.Post
import com.vaveylax.yeniappwkotlin.databinding.FragmentHomeRowItemBinding

interface RecyclerViewClickListener {
    fun onRecyclerViewItemClick(view : View, post : Post, homeRowItemBinding: FragmentHomeRowItemBinding)
}