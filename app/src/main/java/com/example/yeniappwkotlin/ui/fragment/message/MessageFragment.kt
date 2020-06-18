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
import com.example.yeniappwkotlin.util.Coroutines
import com.example.yeniappwkotlin.util.PrefUtils
import com.example.yeniappwkotlin.util.loadImage
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

        val networkConnectionInterceptor = NetworkConnectionInterceptor(requireContext())
        val api = MyApi(networkConnectionInterceptor)
        val db = AppDatabase(requireContext())
        val repository = MessageListRepository(api, db, requireContext())
        val userId = PrefUtils.with(requireContext()).getInt("user_id", 0)
        val factory = MessageViewModelFactory(repository, userId)
        viewModel = ViewModelProviders.of(this, factory).get(MessageViewModel::class.java)

        Coroutines.main {
            try {
                val messageList = viewModel.getMessageList.await()
                messageList.observe(viewLifecycleOwner, Observer {messages ->
                    messageRecycler.also {
                        it.layoutManager = LinearLayoutManager(requireContext())
                        it.setHasFixedSize(true)
                        it.adapter = MessageFragmentAdapter(requireContext(), messages, this)
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
            if (userId == message.alici_id){
                it.putExtra("alici_name",message.name)
                it.putExtra("photo",message.paths)
                it.putExtra("post_sahibi_id",message.gonderen_id)
            }else{
                it.putExtra("alici_name",message.alici_name)
                it.putExtra("photo",message.alici_photo)
                it.putExtra("post_sahibi_id",message.alici_id)
            }
            it.putExtra("message_id",message.messageId)
            startActivity(it)
        }
    }

}
