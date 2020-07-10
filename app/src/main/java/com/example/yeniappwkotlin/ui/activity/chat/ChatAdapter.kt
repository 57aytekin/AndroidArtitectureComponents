package com.example.yeniappwkotlin.ui.activity.chat

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.yeniappwkotlin.R
import com.example.yeniappwkotlin.data.db.entities.Chat
import com.example.yeniappwkotlin.util.PrefUtils
import com.example.yeniappwkotlin.util.calculateDate
import com.example.yeniappwkotlin.util.convertTimestamp
import kotlinx.android.synthetic.main.chat_item_right.view.*
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter(
    val context: Context,
    private val chatList : List<Chat>
): RecyclerView.Adapter<ChatAdapter.ViewHolder>() {
    private val MSG_TYPE_RIGHT = 1
    private val MSG_TYPE_LEFT = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        if (viewType == MSG_TYPE_RIGHT){
            return ViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(context),
                    R.layout.chat_item_right,
                    parent,
                    false
                )
            )
        }else{
            return ViewHolder(
                DataBindingUtil.inflate(
                    LayoutInflater.from(context),
                    R.layout.chat_item_left,
                    parent,
                    false
                )
            )
        }
    }

    override fun getItemCount() = chatList.size

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val messageDate = convertTimestamp(chatList[position].tarih!!)
        val dateFormat = SimpleDateFormat("dd.M.yyyy HH:mm:ss")
        //val date1 : Date = dateFormat.parse( chatList[position].tarih!!)!!

        val date0 : Date = Date(chatList[position].tarih!!.toLong() * 1000)
        val date1 : Date = dateFormat.parse(dateFormat.format(date0))!!

        if (holder.itemViewType == MSG_TYPE_RIGHT){
            holder.chatItemBinding.root.show_message.text = chatList[position].message
            if (messageDate.contains("gun") || messageDate.contains("ay")){
                val firebaseDate = date1
                holder.chatItemBinding.root.show_date.text = firebaseDate!!.toLocaleString()
            }else{
                holder.chatItemBinding.root.show_date.text = "${date1.hours}:${date1.minutes}"
            }
        }else {
            holder.chatItemBinding.root.show_message.text = chatList[position].message
            if (messageDate.contains("gun") || messageDate.contains("ay")){
                val firebaseDate = date1
                holder.chatItemBinding.root.show_date.text = firebaseDate!!.toLocaleString()
            }else{
                holder.chatItemBinding.root.show_date.text = "${date1.hours}:${date1.minutes}"
            }
        }
    }

    inner class ViewHolder (
        val chatItemBinding: ViewDataBinding
    ) : RecyclerView.ViewHolder(chatItemBinding.root)

    override fun getItemViewType(position: Int): Int {
        val userId = PrefUtils.with(context).getInt("user_id",-1)
        return if (userId.toString() == chatList[position].gonderen ){
            MSG_TYPE_RIGHT
        }else {
            MSG_TYPE_LEFT
        }
    }


}