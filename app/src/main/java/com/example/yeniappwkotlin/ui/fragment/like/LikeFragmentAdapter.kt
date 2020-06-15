package com.example.yeniappwkotlin.ui.fragment.like

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.yeniappwkotlin.R
import com.example.yeniappwkotlin.data.db.entities.Likes
import com.example.yeniappwkotlin.databinding.LikeFragmentRowItemBinding
import com.example.yeniappwkotlin.util.loadImage

class LikeFragmentAdapter(
    private val likes : List<Likes>
) : RecyclerView.Adapter<LikeFragmentAdapter.LikesViewHolder>() {
    val likesText = " adlı kullanıcı yorumunu beğendi artık mesaj atıp konuşabilirsin."

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

    override fun onBindViewHolder(holder: LikesViewHolder, position: Int) {
        val currentItem = likes[position]
        loadImage(holder.likeFragmentRowItemBinding.likeRowPhoto, currentItem.paths)
        val username = currentItem.name

        val sb: SpannableStringBuilder? = SpannableStringBuilder(username)
        val bss = StyleSpan(Typeface.BOLD)
        sb!!.setSpan(bss,0,username.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        holder.likeFragmentRowItemBinding.likeRowUserName.text = "$sb $likesText"
    }
}