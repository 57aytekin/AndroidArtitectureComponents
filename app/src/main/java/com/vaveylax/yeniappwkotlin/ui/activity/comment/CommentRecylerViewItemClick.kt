package com.vaveylax.yeniappwkotlin.ui.activity.comment

import android.view.View
import com.vaveylax.yeniappwkotlin.data.db.entities.Comment
import com.vaveylax.yeniappwkotlin.databinding.CommentRowItem2Binding

interface CommentRecyclerViewItemClick {
    fun onRecyclerViewCheckUncheck(view: View, comment: Comment, isChecked : Boolean, commentRowItemBinding: CommentRowItem2Binding)
}