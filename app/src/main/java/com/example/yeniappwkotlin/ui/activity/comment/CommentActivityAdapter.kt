package com.example.yeniappwkotlin.ui.activity.comment

import android.text.Html
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.yeniappwkotlin.R
import com.example.yeniappwkotlin.data.db.entities.Comment
import com.example.yeniappwkotlin.databinding.CommentRowItem2Binding

class CommentActivityAdapter(
    private val comment : List<Comment>,
    private val post_id : Int
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
        val commentUserName = comments.name
        val userComment = comments.comment
        val boldType = "$commentUserName  $userComment"
        holder.commentRowItemBinding.tvCommentAd.text = Html.escapeHtml(boldType)
        holder.commentRowItemBinding.tvCommentTarih.text = comments.tarih


    }

    inner class CommentViewHolder(
        val commentRowItemBinding: CommentRowItem2Binding
    ) : RecyclerView.ViewHolder(commentRowItemBinding.root)
}