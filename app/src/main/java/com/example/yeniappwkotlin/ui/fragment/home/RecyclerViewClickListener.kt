package com.example.yeniappwkotlin.ui.fragment.home

import android.view.View
import com.example.yeniappwkotlin.data.db.entities.Post
import com.example.yeniappwkotlin.databinding.FragmentHomeRowItemBinding

interface RecyclerViewClickListener {
    fun onRecyclerViewItemClick(view : View, post : Post)
    fun onRecyclerViewCheckUnckeck(view: View, post: Post, isChecked : Boolean, homeRowItemBinding: FragmentHomeRowItemBinding)
}