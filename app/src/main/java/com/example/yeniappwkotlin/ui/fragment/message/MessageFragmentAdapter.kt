package com.example.yeniappwkotlin.ui.fragment.message

import android.content.Context
import android.view.LayoutInflater
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

        if (userId == currentMessage.alici_id){
            bindingView.messageItemUsername.text = currentMessage.name
            loadImage(bindingView.messageItemUserPhoto, currentMessage.paths)
        }else{
            bindingView.messageItemUsername.text = currentMessage.alici_name
            loadImage(bindingView.messageItemUserPhoto, currentMessage.alici_photo)
        }

        //Glide.with(context).load(currentMessage.alici_photo).into(bindingView.messageItemUserPhoto)

        bindingView.messageItemMesaj.text = currentMessage.message
        bindingView.messageItemDate.text = calculateDate(currentMessage.tarih)

        bindingView.root.setOnClickListener {
            listener.onRecyclerViewItemClick(bindingView.root, currentMessage)
        }
    }
}