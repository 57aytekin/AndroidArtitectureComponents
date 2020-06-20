package com.example.yeniappwkotlin.ui.activity.chat

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.yeniappwkotlin.R
import com.example.yeniappwkotlin.data.db.database.AppDatabase
import com.example.yeniappwkotlin.data.db.entities.Chat
import com.example.yeniappwkotlin.data.db.entities.MessageList
import com.example.yeniappwkotlin.data.network.MyApi
import com.example.yeniappwkotlin.data.network.NetworkConnectionInterceptor
import com.example.yeniappwkotlin.data.network.repositories.ChatRepository
import com.example.yeniappwkotlin.databinding.ActivityChatBinding
import com.example.yeniappwkotlin.util.Coroutines
import com.example.yeniappwkotlin.util.PrefUtils
import com.example.yeniappwkotlin.util.loadImage
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chat.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {
    private lateinit var binding : ActivityChatBinding
    private lateinit var viewModel: ChatViewModel
    private var alici_name: String? = null
    private var photo: String? = null
    private var post_sahibi_id: Int? = null
    private var messageId: Int? = null
    private var references : DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val networkConnectionInterceptor  = NetworkConnectionInterceptor(this)
        val api = MyApi(networkConnectionInterceptor)
        val db = AppDatabase(this)
        val repository = ChatRepository(api, db, this)
        val factory = ChatViewModelFactory(repository)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat)
        viewModel = ViewModelProvider(this, factory).get(ChatViewModel::class.java)

        messageId = intent.getIntExtra("message_id",-1)
        alici_name = intent.getStringExtra("alici_name")
        photo = intent.getStringExtra("photo")
        post_sahibi_id = intent.getIntExtra("post_sahibi_id",-1)
        val prefUtil = PrefUtils.with(this)
        val userId = prefUtil.getInt("user_id",-1)
        val isSocial = prefUtil.getInt("is_social_account", 0)
        val currentDate = SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.ENGLISH).format(Date())

        //initialize object
        binding.tvMesajlasmaUsername.text = alici_name
        //Glide.with(this).load(photo).into(binding.ivMesajlasmaPhoto)
        loadImage(binding.ivMesajlasmaPhoto, photo, isSocial)
        Log.d("IMAGE", photo.toString())

        //Send message and save database operations
        binding.btnMesajlasma.setOnClickListener {
            var flag = false
            val userMessage = binding.etMesaj.text.toString()
            if (userMessage.isNotEmpty()){
                viewModel.saveChats(userId.toString(), post_sahibi_id.toString(), alici_name!!, userMessage, photo!!, currentDate)
                val message = db.getMessageListDao().getMessageList()
                message.observe(this, androidx.lifecycle.Observer {messages ->
                    for (mess in messages){
                        if ((mess.alici_id == userId && mess.gonderen_id == post_sahibi_id) ||
                            mess.alici_id == post_sahibi_id && mess.gonderen_id == userId){
                            flag = true
                            viewModel.updateMessageList(messageId!!, userMessage)
                            viewModel.updateLocalMessageList(currentDate, userMessage, messageId!!)
                            break
                        }
                    }
                    if (!flag){
                        viewModel.saveMessageList(userId, post_sahibi_id!!, alici_name!!, photo!!, userMessage)
                    }
                })
            }
            binding.etMesaj.setText("")
        }
        binding.mesajlasmaBack.setOnClickListener { finish() }

        //listen firebase database
        references = FirebaseDatabase.getInstance().getReference("Chats")
        references!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                readChatList(userId.toString(), post_sahibi_id.toString())
            }

            override fun onCancelled(p0: DatabaseError) {
                Log.d("Log","Cancel")
            }
        })

    }

    //Burası repository taşınacak
    fun readChatList(gonderen : String, alici : String){
        val chatList = ArrayList<Chat>()
        val postListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!chatList.isNullOrEmpty()){
                    chatList.clear()
                }
                try {
                    for (data in snapshot.children) {
                        val chat: Chat = data.getValue(Chat::class.java)!!
                        if ((alici == chat.alici && gonderen == chat.gonderen) || (alici == chat.gonderen && gonderen == chat.alici)){
                            chatList.add(chat)
                        }
                    }
                    recyclerMesajlasma.also {
                        it.layoutManager = LinearLayoutManager(this@ChatActivity)
                        it.setHasFixedSize(true)
                        it.adapter = ChatAdapter(this@ChatActivity, chatList)
                        it.smoothScrollToPosition((it.adapter as ChatAdapter).itemCount)
                    }
                }catch (e : Exception){
                    Log.d("ERRR: ",e.message!!)
                }


            }

            override fun onCancelled(p0: DatabaseError) {
                Log.w(ContentValues.TAG, "loadPost:onCancelled", p0.toException())
            }
        }
        references!!.addValueEventListener(postListener)
    }

}