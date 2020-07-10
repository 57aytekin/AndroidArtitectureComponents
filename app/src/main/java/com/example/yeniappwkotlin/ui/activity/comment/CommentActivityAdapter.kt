package com.example.yeniappwkotlin.ui.activity.comment

import android.text.Html
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.yeniappwkotlin.R
import com.example.yeniappwkotlin.data.db.entities.Comment
import com.example.yeniappwkotlin.databinding.CommentRowItem2Binding
import com.example.yeniappwkotlin.ui.fragment.home.RecyclerViewClickListener
import com.example.yeniappwkotlin.util.calculateDate
import com.example.yeniappwkotlin.util.convertTimestamp
import com.example.yeniappwkotlin.util.loadImage
import java.io.FileNotFoundException

class CommentActivityAdapter(
    private val comment: List<Comment>,
    private val user_id: Int,
    private val post_user_id: Int,
    private val listener: CommentRecyclerViewItemClick
) : RecyclerView.Adapter<CommentActivityAdapter.CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CommentViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.comment_row_item2,
                parent,
                false
            )
        )

    override fun getItemCount() = comment.size

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comments = comment[position]
        val commentUserName = comments.user_name
        val userComment = comments.comment
        val boldType = "$commentUserName $userComment"

        val sb: SpannableStringBuilder? = SpannableStringBuilder(boldType)
        val bss = StyleSpan(android.graphics.Typeface.BOLD)
        sb!!.setSpan(bss, 0, commentUserName!!.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)

        if (comments.user_id != user_id && post_user_id == user_id) {
            holder.commentRowItemBinding.toggle.visibility = View.VISIBLE
            if (comments.begeniDurum == 1) {
                holder.commentRowItemBinding.toggle.setBackgroundResource(R.drawable.ic_favorite_orange)
                holder.commentRowItemBinding.toggle.isChecked = true
            } else {
                holder.commentRowItemBinding.toggle.setBackgroundResource(R.drawable.ic_favorite_black_24dp)
                holder.commentRowItemBinding.toggle.isChecked = false
            }
        } else {
            holder.commentRowItemBinding.toggle.visibility = View.GONE
        }

        holder.commentRowItemBinding.tvCommentAd.text = sb
        holder.commentRowItemBinding.tvCommentTarih.text = convertTimestamp(comments.tarih!!)
        loadImage(holder.commentRowItemBinding.ivCommentPhoto, comments.paths,comments.is_social_account)

        holder.commentRowItemBinding.toggle.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                listener.onRecyclerViewCheckUncheck(holder.commentRowItemBinding.toggle, comments, true, holder.commentRowItemBinding)
            }else{
                listener.onRecyclerViewCheckUncheck(holder.commentRowItemBinding.toggle, comments, false, holder.commentRowItemBinding)
            }
        }
    }

    inner class CommentViewHolder(
        val commentRowItemBinding: CommentRowItem2Binding
    ) : RecyclerView.ViewHolder(commentRowItemBinding.root)
}