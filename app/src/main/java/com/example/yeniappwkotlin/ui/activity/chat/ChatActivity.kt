package com.example.yeniappwkotlin.ui.activity.chat

import android.content.ContentValues
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.yeniappwkotlin.R
import com.example.yeniappwkotlin.data.db.database.AppDatabase
import com.example.yeniappwkotlin.data.db.entities.Chat
import com.example.yeniappwkotlin.data.db.entities.MessageList
import com.example.yeniappwkotlin.data.db.entities.User
import com.example.yeniappwkotlin.data.network.MyApi
import com.example.yeniappwkotlin.data.network.NetworkConnectionInterceptor
import com.example.yeniappwkotlin.data.network.repositories.ChatRepository
import com.example.yeniappwkotlin.data.network.responses.CommentResponse
import com.example.yeniappwkotlin.databinding.ActivityChatBinding
import com.example.yeniappwkotlin.util.*
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
    private var isSocial : Int? = null
    private var references : DatabaseReference? = null
    private var userId: Int? =null
    private val chatList = ArrayList<Chat>()
    private var adapter : ChatAdapter? =null
    private var recyclerView : RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val networkConnectionInterceptor  = NetworkConnectionInterceptor(this)
        val api = MyApi(networkConnectionInterceptor)
        val db = AppDatabase(this)
        val repository = ChatRepository(api, db, this)
        val factory = ChatViewModelFactory(repository)

        viewModel = ViewModelProvider(this, factory).get(ChatViewModel::class.java)

        messageId = intent.getIntExtra("message_id",-1)
        isSocial = intent.getIntExtra("is_social",-1)
        alici_name = intent.getStringExtra("alici_name")
        val aliciUserName = intent.getStringExtra("alici_username")
        photo = intent.getStringExtra("photo")
        post_sahibi_id = intent.getIntExtra("post_sahibi_id",-1)
        val prefUtil = PrefUtils.with(this)
        userId = prefUtil.getInt("user_id",-1)
        val currentDate = SimpleDateFormat("dd.M.yyyy HH:mm:ss", Locale.ENGLISH).format(Date())

        //Update Is see new message
        Coroutines.main { viewModel.updateIsSeeMessage(userId!!,1) }

        //initialize object
        tvMesajlasmaUsername.text = alici_name
        tvMesajlasmaName.text = "@${aliciUserName}"
        loadImage(ivMesajlasmaPhoto, photo, isSocial)
        //Update new message badges
        updateNewMessageBadges(db, userId!!)

        //Send message and save database operations
        btnMesajlasma.setOnClickListener {
            var flag = false
            val userMessage = etMesaj.text.toString()
            if (userMessage.isNotEmpty()){
                viewModel.saveChats(userId.toString(), post_sahibi_id.toString(), alici_name!!, userMessage, photo!!, currentDate)
                //val message = db.getMessageListDao().getMessageList()
                Coroutines.main {
                    try{
                        val getMessage = viewModel.getMessageList(userId!!)
                        for (mess in getMessage){
                            if ((mess.alici_user.user_id == userId && mess.gonderen_user.user_id == post_sahibi_id) ||
                                mess.alici_user.user_id == post_sahibi_id && mess.gonderen_user.user_id == userId){
                                if (userId == mess.gonderen_user.user_id){
                                    viewModel.updateMessageList(mess.messageId!!, mess.alici_user.user_id!!, userMessage, mess.alici_new_message_count+1, mess.gonderen_new_message_count,0,0)
                                    viewModel.updateLocalMessageList(currentDate, userMessage, mess.messageId, mess.alici_new_message_count+1, mess.gonderen_new_message_count)
                                    flag = true
                                }
                                else if (userId == mess.alici_user.user_id){
                                    viewModel.updateMessageList(mess.messageId!!, mess.gonderen_user.user_id!!, userMessage, mess.alici_new_message_count, mess.gonderen_new_message_count+1,0,0)
                                    viewModel.updateLocalMessageList(currentDate, userMessage, mess.messageId, mess.alici_new_message_count, mess.gonderen_new_message_count+1)
                                    flag = true
                                }
                            }
                        }
                        if (!flag){
                            viewModel.saveMessageList(userId!!, post_sahibi_id!!, userMessage, 1, 0)
                            viewModel.saveLocalMessageList(MessageList(
                                message = userMessage, tarih = currentDate, alici_new_message_count = 1, gonderen_new_message_count = 0,
                                gonderen_user = User(user_id = userId), alici_user = User(
                                    user_id = post_sahibi_id, paths = photo, first_name = alici_name, is_social_account = isSocial )
                            ))
                        }
                    }catch (e: ApiException) {
                        toast("Sunucudan yanıt alınamıyor")
                    } catch (e: NoInternetException) {
                        toast("İnternet bağlantınızı kontrol ediniz")
                    } catch (e : java.lang.Exception){
                        toast("Bir hata oluştu: "+ e.printStackTrace().toString())
                    }
                }
            }
            etMesaj.setText("")
        }
        mesajlasmaBack.setOnClickListener { finish() }

        //listen firebase database
        adapter = ChatAdapter(this, chatList)
        recyclerView = recyclerMesajlasma
        try {
            references = FirebaseDatabase.getInstance().getReference("Chats")

            references!!.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    readChatList(userId.toString(), post_sahibi_id.toString(), p0)
                }
                override fun onCancelled(p0: DatabaseError) {
                    Log.d("Log","Cancel")
                }
            })
            recyclerView.also {
                it!!.layoutManager = LinearLayoutManager(this@ChatActivity)
                it.setHasFixedSize(true)
                it.adapter = adapter
                it.smoothScrollToPosition((it.adapter as ChatAdapter).itemCount)
            }

        }catch (e : Exception){
            e.printStackTrace()
        }

    }

    override fun onPause() {
        super.onPause()
        if(isFinishing){
            Coroutines.main {
                viewModel.updateIsSeeMessage(userId!!, 0)
            }
        }
    }

    private fun updateNewMessageBadges(db : AppDatabase, userId : Int) {
        var exitFlag = false
        val message = db.getMessageListDao().getMessageList()
        message.observe(this, androidx.lifecycle.Observer {messages ->
            if (!exitFlag){
                for (mess in messages){
                    exitFlag = if (userId != mess.gonderen_user.user_id){
                        viewModel.updateMessageList(messageId!!, mess.gonderen_user.user_id!!, "", 0, mess.gonderen_new_message_count,1,0)
                        viewModel.updateLocalMessageBadges( messageId!!, 0, mess.gonderen_new_message_count)
                        true
                    }else{
                        viewModel.updateMessageList(messageId!!, mess.alici_user.user_id!!, "", mess.alici_new_message_count, 0,0,1)
                        viewModel.updateLocalMessageBadges( messageId!!, mess.alici_new_message_count, 0)
                        true
                    }
                }
            }
        })
    }

    //Burası repository taşınacak
    fun readChatList(gonderen : String, alici : String, snapshot: DataSnapshot){
        if (!chatList.isNullOrEmpty()){
            chatList.clear()
        }
        try {
            for (data in snapshot.children) {
                val chat: Chat = data.getValue(Chat::class.java)!!
                if ((alici == chat.alici && gonderen == chat.gonderen) || (alici == chat.gonderen && gonderen == chat.alici)){
                    chatList.add(chat)
                    adapter!!.notifyDataSetChanged()
                    recyclerView!!.smoothScrollToPosition((recyclerView!!.adapter as ChatAdapter).itemCount)
                }
            }
        }catch (e : Exception){
            Log.d("ERRR: ",e.message!!)
        }
    }
}