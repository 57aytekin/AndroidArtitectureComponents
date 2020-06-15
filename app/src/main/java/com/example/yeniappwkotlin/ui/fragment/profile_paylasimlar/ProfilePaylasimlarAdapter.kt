package com.example.yeniappwkotlin.ui.fragment.profile_paylasimlar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.yeniappwkotlin.R
import com.example.yeniappwkotlin.data.db.entities.Post
import com.example.yeniappwkotlin.databinding.ProfilePaylasimlarRowItemBinding

class ProfilePaylasimlarAdapter(
    private val posts: List<Post>
) : RecyclerView.Adapter<ProfilePaylasimlarAdapter.ViewHolder>() {

    inner class ViewHolder(
        val profilePaylasimlarRowItemBinding: ProfilePaylasimlarRowItemBinding
    ) : RecyclerView.ViewHolder(profilePaylasimlarRowItemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.profile_paylasimlar_row_item,
                parent,
                false
            )
        )

    override fun getItemCount() = posts.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentPost = posts[position]
        holder.profilePaylasimlarRowItemBinding.profilePaylasimCommentCount.text = "${currentPost.comment_count.toString()} Yorum"
        holder.profilePaylasimlarRowItemBinding.profilePaylasimLikeCount.text = "${currentPost.like_count.toString()} Beğeni"
        holder.profilePaylasimlarRowItemBinding.profilePaylasimPostText.text = currentPost.share_post.toString()
        holder.profilePaylasimlarRowItemBinding.profilePaylasimTarih.text = currentPost.tarih.toString()
    }
}