package com.vaveylax.yeniappwkotlin.ui.fragment.like

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.vaveylax.yeniappwkotlin.R
import com.vaveylax.yeniappwkotlin.data.db.entities.Likes
import com.vaveylax.yeniappwkotlin.databinding.LikeFragmentRowItemBinding
import com.vaveylax.yeniappwkotlin.ui.activity.comment.CommentActivity
import com.vaveylax.yeniappwkotlin.util.convertTimestamp
import com.vaveylax.yeniappwkotlin.util.loadImage
import kotlinx.android.synthetic.main.like_fragment_row_item.view.*

class LikeFragmentAdapter(
    private val likes : List<Likes>,
    private val listener : ClickListener,
    private val context : Context
) : RecyclerView.Adapter<LikeFragmentAdapter.LikesViewHolder>() {
    var lastestId = 0
    inner class LikesViewHolder(
        val likeFragmentRowItemBinding: LikeFragmentRowItemBinding
    ) : RecyclerView.ViewHolder(likeFragmentRowItemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        LikesViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.like_fragment_row_item,
                parent,
                false
            )
        )

    override fun getItemCount() = likes.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: LikesViewHolder, position: Int) {
        var likesText = ""
        val currentItem = likes[position]
        if (currentItem.comment_id == -1 && currentItem.post_sahibi_id != -1){
            likesText = " adlı kullanıcı '${currentItem.share_post}' paylaşımını beğendi."
            holder.likeFragmentRowItemBinding.likeRowBtn.visibility = View.INVISIBLE
        }else if (currentItem.comment_id == -1 && currentItem.post_sahibi_id == -1){
            likesText = " adlı kullanıcı '${currentItem.share_post}' paylaşımına '${currentItem.comment}' tamamlamasını yaptı."
            holder.likeFragmentRowItemBinding.likeRowBtn.visibility = View.INVISIBLE
        } else{
            val count = frequency(likes, currentItem.post_sahibi_id)
            if (count > 1 && currentItem.likes_id != lastestId){
                holder.likeFragmentRowItemBinding.likeRowBtn.visibility = View.INVISIBLE
                likesText = " adlı kullanıcı '${currentItem.comment}' tamamlamanı beğendi."
            }else{
                holder.likeFragmentRowItemBinding.likeRowBtn.visibility = View.VISIBLE
                likesText = " adlı kullanıcı '${currentItem.comment}' tamamlamanı beğendi. Artık onunla iletişime geçebilirsin."
            }
        }
        loadImage(holder.likeFragmentRowItemBinding.likeRowPhoto, currentItem.paths, currentItem.is_social_account)
        val username = currentItem.user_name

        val sb: SpannableStringBuilder? = SpannableStringBuilder(username)
        val bss = StyleSpan(Typeface.BOLD)
        sb!!.setSpan(bss,0,username.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        holder.likeFragmentRowItemBinding.likeRowUserName.text = "'${sb}' $likesText"
        holder.likeFragmentRowItemBinding.likeRowDate.text = convertTimestamp(currentItem.tarih.toString())

        holder.likeFragmentRowItemBinding.root.setOnClickListener {
            if (currentItem.comment_id == -1 && currentItem.post_sahibi_id == -1){
                Log.d("USERID",currentItem.user_id.toString())
                val intent = Intent(context, CommentActivity::class.java)
                intent.putExtra("post_id", currentItem.post_id)
                intent.putExtra("post_name", currentItem.user_name)
                intent.putExtra("post_user_id", currentItem.user_id)
                intent.putExtra("path", currentItem.paths)
                context.startActivity(intent)
            }
        }

        holder.likeFragmentRowItemBinding.likeRowBtn.setOnClickListener {
            listener.onRecyclerViewItemClick(holder.itemView.likeRowBtn, currentItem)
        }
        holder.likeFragmentRowItemBinding.likeRowPhoto.setOnClickListener {
            listener.onRecyclerViewItemClick(holder.itemView.likeRowPhoto, currentItem)
        }
    }

    private fun frequency(liste : List<Likes>, post_sahibi : Int) : Int{
        var result = 0
        for(item in liste){
            if (post_sahibi == item.post_sahibi_id){
                lastestId = item.likes_id
                result ++
            }
        }
        return result
    }
}