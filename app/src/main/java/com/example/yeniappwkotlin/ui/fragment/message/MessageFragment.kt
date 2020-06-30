package com.example.yeniappwkotlin.ui.fragment.message

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.yeniappwkotlin.R
import com.example.yeniappwkotlin.data.db.database.AppDatabase
import com.example.yeniappwkotlin.data.db.entities.MessageList
import com.example.yeniappwkotlin.data.network.MyApi
import com.example.yeniappwkotlin.data.network.NetworkConnectionInterceptor
import com.example.yeniappwkotlin.data.network.repositories.MessageListRepository
import com.example.yeniappwkotlin.ui.activity.chat.ChatActivity
import com.example.yeniappwkotlin.util.*
import kotlinx.android.synthetic.main.appbar_likes.*
import kotlinx.android.synthetic.main.appbar_message.*
import kotlinx.android.synthetic.main.like_fragment.*
import kotlinx.android.synthetic.main.message_fragment.*

class MessageFragment : Fragment(), MessageClickListener {

    companion object {
        fun newInstance() = MessageFragment()
    }

    private lateinit var viewModel: MessageViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.message_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        openCloseSoftKeyboard(requireContext(),requireView(), false)

        val networkConnectionInterceptor = NetworkConnectionInterceptor(requireContext())
        val api = MyApi(networkConnectionInterceptor)
        val db = AppDatabase(requireContext())
        val repository = MessageListRepository(api, db, requireContext())
        val userId = PrefUtils.with(requireContext()).getInt("user_id", 0)
        val factory = MessageViewModelFactory(repository, userId)
        viewModel = ViewModelProviders.of(this, factory).get(MessageViewModel::class.java)

        //loadAppBarUserPhoto
        val userPhoto = PrefUtils.with(requireContext()).getString("user_image","")
        val isSocial = PrefUtils.with(requireContext()).getInt("is_social_account",0)
        loadImage(ivMessagePhoto, userPhoto, isSocial)

        Coroutines.main {
            try {
                message_progress_bar.show()
                val messageList = viewModel.getMessageList.await()
                messageList.observe(viewLifecycleOwner, Observer {messages ->
                    if (messages.isEmpty()){
                        tvEmptyMessage.visibility = View.VISIBLE
                        message_progress_bar.hide()
                    }else{
                        tvEmptyMessage.visibility = View.INVISIBLE
                        message_progress_bar.hide()
                        messageRecycler.also {
                            it.layoutManager = LinearLayoutManager(requireContext())
                            it.setHasFixedSize(true)
                            it.adapter = MessageFragmentAdapter(requireContext(), messages,this)
                        }
                    }

                })
            }catch (e : Exception){
                Log.d("MESSAGE_FRAGMENT",e.message!!)
            }
        }
    }

    override fun onRecyclerViewItemClick(view: View, message: MessageList) {
        val userId = PrefUtils.with(requireContext()).getInt("user_id",-1)
        Intent(requireContext(), ChatActivity::class.java).also {
            if (userId == message.gonderen_user.user_id){
                it.putExtra("photo",message.alici_user.paths)
                it.putExtra("alici_name",message.alici_user.first_name)
                it.putExtra("alici_username",message.alici_user.user_name)
                it.putExtra("post_sahibi_id",message.alici_user.user_id)
                it.putExtra("is_social",message.alici_user.is_social_account)
            }else{
                it.putExtra("alici_name",message.gonderen_user.first_name)
                it.putExtra("alici_username",message.gonderen_user.user_name)
                it.putExtra("photo",message.gonderen_user.paths)
                it.putExtra("post_sahibi_id",message.gonderen_user.user_id)
                it.putExtra("is_social",message.gonderen_user.is_social_account)
            }
            it.putExtra("message_id",message.messageId)
            startActivity(it)
        }
    }

}
