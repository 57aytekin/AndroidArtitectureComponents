package com.example.yeniappwkotlin.ui.fragment.message

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.yeniappwkotlin.R
import com.example.yeniappwkotlin.data.db.entities.MessageList
import com.example.yeniappwkotlin.databinding.MessageRowItemBinding
import com.example.yeniappwkotlin.ui.fragment.home.RecyclerViewClickListener
import com.example.yeniappwkotlin.util.PrefUtils
import com.example.yeniappwkotlin.util.calculateDate
import com.example.yeniappwkotlin.util.convertTimestamp
import com.example.yeniappwkotlin.util.loadImage

class MessageFragmentAdapter(
    private val context: Context,
    private val messageList : List<MessageList>,
    private val listener : MessageClickListener

) : RecyclerView.Adapter<MessageFragmentAdapter.ViewHolder>() {

    inner class ViewHolder (val messageRowItemBinding: MessageRowItemBinding): RecyclerView.ViewHolder(messageRowItemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.message_row_item,
                parent,
                false
            )
        )

    override fun getItemCount() = messageList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        val bindingView = holder.messageRowItemBinding
        val userId = PrefUtils.with(context).getInt("user_id",-1)

        if (userId == currentMessage.gonderen_user.user_id){
            bindingView.messageItemUsername.text = currentMessage.alici_user.first_name
            loadImage(bindingView.messageItemUserPhoto, currentMessage.alici_user.paths,currentMessage.alici_user.is_social_account)
            if (currentMessage.gonderen_new_message_count > 0){
                bindingView.messageItemNewCount.visibility = View.VISIBLE
                bindingView.messageItemNewCount.text = currentMessage.gonderen_new_message_count.toString()
                bindingView.messageItemMesaj.typeface = Typeface.DEFAULT_BOLD
            }else{
                bindingView.messageItemNewCount.visibility = View.INVISIBLE
                bindingView.messageItemMesaj.typeface = Typeface.DEFAULT
            }
        }else{
            bindingView.messageItemUsername.text = currentMessage.gonderen_user.first_name
            loadImage(bindingView.messageItemUserPhoto, currentMessage.gonderen_user.paths,currentMessage.gonderen_user.is_social_account)
            if (currentMessage.alici_new_message_count > 0){
                bindingView.messageItemNewCount.visibility = View.VISIBLE
                bindingView.messageItemNewCount.text = currentMessage.alici_new_message_count.toString()
                bindingView.messageItemMesaj.typeface = Typeface.DEFAULT_BOLD
            }else{
                bindingView.messageItemNewCount.visibility = View.INVISIBLE
                bindingView.messageItemMesaj.typeface = Typeface.DEFAULT
            }
        }

        //Glide.with(context).load(currentMessage.alici_photo).into(bindingView.messageItemUserPhoto)

        bindingView.messageItemMesaj.text = currentMessage.message
        bindingView.messageItemDate.text = convertTimestamp(currentMessage.tarih)

        bindingView.root.setOnClickListener {
            listener.onRecyclerViewItemClick(bindingView.root, currentMessage)
        }
    }
}