package com.example.yeniappwkotlin.ui.fragment.home

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.yeniappwkotlin.R
import com.example.yeniappwkotlin.data.db.entities.Post
import com.example.yeniappwkotlin.data.db.entities.PostLikes
import com.example.yeniappwkotlin.databinding.FragmentHomeRowItemBinding
import com.example.yeniappwkotlin.util.loadImage
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.math.abs

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class HomeFragmentAdapter(
    private val posts : List<Post>,
    private val listener: RecyclerViewClickListener
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
        val newDate = posts[position].tarih?.let { calculateDate(it) }
        val newPost = Post(
            posts[position].post_id,
            posts[position].user_id,
            posts[position].name,
            posts[position].paths,
            posts[position].share_post,
            posts[position].like_count,
            posts[position].comment_count,
            newDate,
            posts[position].user_post_likes
        )
        updateUI(holder.homeRowItemBinding, newPost)
        holder.homeRowItemBinding.postBtnComment.setOnClickListener {
            listener.onRecyclerViewItemClick(holder.homeRowItemBinding.postBtnComment, posts[position])
        }
        holder.homeRowItemBinding.postCommentCount.setOnClickListener {
            listener.onRecyclerViewItemClick(holder.homeRowItemBinding.postCommentCount, posts[position])
        }
        holder.homeRowItemBinding.postBtnLike.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                listener.onRecyclerViewCheckUnckeck(holder.homeRowItemBinding.postBtnLike, posts[position], true, holder.homeRowItemBinding)
            }else{
                listener.onRecyclerViewCheckUnckeck(holder.homeRowItemBinding.postBtnLike, posts[position], false, holder.homeRowItemBinding)
            }
        }
    }

    private fun updateUI(homeRowItemBinding: FragmentHomeRowItemBinding, post: Post){
        loadImage(homeRowItemBinding.postProfileResim, post.paths)
        homeRowItemBinding.postUsername.text = post.name
        homeRowItemBinding.postTarih.text = post.tarih
        homeRowItemBinding.postText.text = post.share_post
        val likeCount = post.like_count
        val commentCount = post.comment_count
        homeRowItemBinding.postLikeCount.text = "$likeCount Beğeni"
        homeRowItemBinding.postCommentCount.text = "$commentCount Yorum"
        if (post.user_post_likes?.begeni_durum == 1){
            homeRowItemBinding.postBtnLike.isChecked = true
        }
    }

    inner class PostViewHolder(
        val homeRowItemBinding: FragmentHomeRowItemBinding
    ): RecyclerView.ViewHolder(homeRowItemBinding.root)


    @SuppressLint("SimpleDateFormat")
    fun calculateDate (date : String) : String{
        val asd  = StringBuilder()
        val dateFormat = SimpleDateFormat("dd.M.yyyy hh:mm:ss")
        try {
            val date1 : Date = dateFormat.parse(date)
            val bugun  = Calendar.getInstance().time

            val difference : Long = abs(date1.time - bugun.time)
            val dakika : Long = difference / (1000*60)
            val saat : Long  = difference / (1000*60*60)
            val gun : Long = difference / (1000*60*60*24)

            val ay_bilgisi : Int = gun.toInt() /30

            if(dakika.toInt() == 0){
                asd.append("Az önce")
            }else if(ay_bilgisi == 0 && gun.toInt() == 0 && saat.toInt() == 0){
                asd.append(dakika.toInt())
                asd.append(" dk önce")

            }else if(ay_bilgisi == 0 && gun.toInt() == 0){
                asd.append(saat.toInt())
                asd.append(" saat önce")

            }else if(ay_bilgisi == 0){
                asd.append(gun.toInt())
                asd.append(" gun önce")
            }else {
                asd.append(ay_bilgisi)
                asd.append(" ay önce")
            }
        }catch (e: ParseException){
            e.printStackTrace()
        }
        return asd.toString()
    }
}