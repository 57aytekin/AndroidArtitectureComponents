package com.example.yeniappwkotlin.ui.activity.comment

import android.view.View
import com.example.yeniappwkotlin.data.db.entities.Comment
import com.example.yeniappwkotlin.databinding.CommentRowItem2Binding

interface CommentRecyclerViewItemClick {
    fun onRecyclerViewCheckUncheck(view: View, comment: Comment, isChecked : Boolean, commentRowItemBinding: CommentRowItem2Binding)
}