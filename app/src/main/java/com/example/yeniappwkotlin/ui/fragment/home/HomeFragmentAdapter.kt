package com.example.yeniappwkotlin.ui.fragment.home

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.yeniappwkotlin.R
import com.example.yeniappwkotlin.data.db.entities.Post
import com.example.yeniappwkotlin.databinding.FragmentHomeRowItemBinding
import com.example.yeniappwkotlin.ui.activity.comment.CommentActivity
import com.example.yeniappwkotlin.util.calculateDate
import com.example.yeniappwkotlin.util.loadImage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class HomeFragmentAdapter(
    private var posts : List<Post>,
    private val listener: RecyclerViewClickListener,
    private val context : Context
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
        if (posts[position].is_deleted != 1){
            val newDate = posts[position].tarih?.let { calculateDate(it) }
            val newPost = Post(
                posts[position].post_id,
                posts[position].user_id,
                posts[position].user_name,
                posts[position].first_name,
                posts[position].paths,
                posts[position].share_post,
                posts[position].like_count,
                posts[position].comment_count,
                newDate,
                posts[position].is_social_account,
                posts[position].is_deleted,
                posts[position].user_post_likes

            )
            updateUI(holder.homeRowItemBinding, newPost)
        }

        holder.homeRowItemBinding.postBtnComment.setOnClickListener {
            listener.onRecyclerViewItemClick(holder.homeRowItemBinding.postBtnComment, posts[position], holder.homeRowItemBinding)
        }
        holder.homeRowItemBinding.postCommentCount.setOnClickListener {
            listener.onRecyclerViewItemClick(holder.homeRowItemBinding.postCommentCount, posts[position],holder.homeRowItemBinding)
        }
        holder.homeRowItemBinding.root.setOnClickListener {
            val intent = Intent(context, CommentActivity::class.java)
            intent.putExtra("post_id", posts[position].post_id)
            intent.putExtra("post_name", posts[position].user_name)
            intent.putExtra("post_user_id", posts[position].user_id)
            intent.putExtra("path", posts[position].paths)
            context.startActivity(intent)
        }
        holder.homeRowItemBinding.homeLikes.setOnClickListener {
            listener.onRecyclerViewItemClick(holder.homeRowItemBinding.homeLikes, posts[position],holder.homeRowItemBinding)
        }
    }

    private fun updateUI(homeRowItemBinding: FragmentHomeRowItemBinding, post: Post){
        loadImage(homeRowItemBinding.postProfileResim, post.paths,post.is_social_account)
        homeRowItemBinding.postUsername.text = "@${post.user_name}"
        homeRowItemBinding.postFirstName.text = post.first_name
        homeRowItemBinding.postTarih.text = post.tarih
        homeRowItemBinding.postText.text = post.share_post
        homeRowItemBinding.postLikeCount.text = post.like_count.toString()
        homeRowItemBinding.postCommentCount.text = post.comment_count.toString()
        if (post.user_post_likes?.begeni_durum == 1){
            homeRowItemBinding.homeLikes.setImageResource(R.drawable.icon_heart)
        }else{
            homeRowItemBinding.homeLikes.setImageResource(R.drawable.icon_heart_gray)
        }
    }
    fun addPost( post : List<Post>){
        CoroutineScope(Dispatchers.IO).launch {
            for(deger in post){
                if (!posts.contains(deger)){
                    posts += deger
                }

            }
        }
    }

    inner class PostViewHolder(
        val homeRowItemBinding: FragmentHomeRowItemBinding
    ): RecyclerView.ViewHolder(homeRowItemBinding.root)
}