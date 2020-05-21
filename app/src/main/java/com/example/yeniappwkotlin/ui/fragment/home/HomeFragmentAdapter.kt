package com.example.yeniappwkotlin.ui.fragment.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.yeniappwkotlin.R
import com.example.yeniappwkotlin.data.db.entities.Post
import com.example.yeniappwkotlin.databinding.FragmentHomeRowItemBinding
import com.xwray.groupie.databinding.BindableItem

class HomeFragmentAdapter(
    private val posts : List<Post>
) : RecyclerView.Adapter<HomeFragmentAdapter.PostViewHolder>() {

    override fun getItemCount() = posts.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PostViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.fragment_home_row_item,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.homeRowItemBinding.post = posts[position]
    }

    inner class PostViewHolder(
        val homeRowItemBinding: FragmentHomeRowItemBinding
    ): RecyclerView.ViewHolder(homeRowItemBinding.root)

}