package com.vaveylax.yeniappwkotlin.ui.activity.chat

import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vaveylax.yeniappwkotlin.R
import com.vaveylax.yeniappwkotlin.data.db.database.AppDatabase
import com.vaveylax.yeniappwkotlin.data.db.entities.Chat
import com.vaveylax.yeniappwkotlin.data.db.entities.MessageList
import com.vaveylax.yeniappwkotlin.data.db.entities.User
import com.vaveylax.yeniappwkotlin.data.network.MyApi
import com.vaveylax.yeniappwkotlin.data.network.NetworkConnectionInterceptor
import com.vaveylax.yeniappwkotlin.data.network.repositories.ChatRepository
import com.vaveylax.yeniappwkotlin.databinding.ActivityChatBinding
import com.vaveylax.yeniappwkotlin.ui.activity.comment.CommentListener
import com.vaveylax.yeniappwkotlin.util.*
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chat.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity(), CommentListener {
    private val chatExp = "CHAT"
    private lateinit var viewModel: ChatViewModel
    private var alici_name: String? = null
    private var aliciUserName: String? = null
    private var photo: String? = null
    private var post_sahibi_id: Int? = null
    private var messageId: Int? = null
    private var isSocial: Int? = null
    private var references: DatabaseReference? = null
    private var userId: Int? = null
    private val chatList = ArrayList<Chat>()
    private var adapter: ChatAdapter? = null
    private var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val networkConnectionInterceptor = NetworkConnectionInterceptor(this)
        val api = MyApi(networkConnectionInterceptor)
        val db = AppDatabase(this)
        val repository = ChatRepository(api, db, this)
        val factory = ChatViewModelFactory(repository)

        viewModel = ViewModelProvider(this, factory).get(ChatViewModel::class.java)
        viewModel.commentListener = this

        messageId = intent.getIntExtra("message_id", -1)
        isSocial = intent.getIntExtra("is_social", -1)
        alici_name = intent.getStringExtra("alici_name")
        aliciUserName = intent.getStringExtra("alici_username")
        photo = intent.getStringExtra("photo")
        post_sahibi_id = intent.getIntExtra("post_sahibi_id", -1)
        val prefUtil = PrefUtils.with(this)
        userId = prefUtil.getInt("user_id", -1)
        val currentDate = (System.currentTimeMillis()/1000).toString()

        //MessageList Budget remov
        PrefUtils.with(this).remove("message_budget_count")

        //Current user data
        val currentUserPhoto = PrefUtils.with(this).getString("user_image", "")
        val currentUserIsSocial = PrefUtils.with(this).getInt("is_social_account", 0)
        //Update Is see new message
        Coroutines.main { viewModel.updateIsSeeMessage(userId!!, post_sahibi_id!!) }

        //initialize object
        tvMesajlasmaUsername.text = alici_name
        tvMesajlasmaName.text = "@${aliciUserName}"
        loadImage(ivMesajlasmaPhoto, photo, isSocial)
        //Load current user photo
        loadImage(chat_user_photo, currentUserPhoto, currentUserIsSocial)
        //Update new message badges
        try {
            updateNewMessageBadges(db, userId!!)
        }catch (e : Exception){
            Log.d(chatExp,e.message!!)
        }

        //When edittext is empty set button color
        btnMesajlasma.disable()
        etMesaj.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Log.d("CHAT", "AfterTextChanged")
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d("CHAT", "BeforeTextChanged")
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().trim().isEmpty()) {
                    btnMesajlasma.disable()
                } else {
                    btnMesajlasma.enable()
                }
            }
        })

        //Send message and save database operations
        btnMesajlasma.setOnClickListener {
            var flag = false
            val userMessage = etMesaj.text.toString().trim()
            if (userMessage.isNotEmpty()) {
                viewModel.saveChats(
                    userId.toString(),
                    post_sahibi_id.toString(),
                    alici_name!!,
                    userMessage,
                    photo!!,
                    currentDate
                )
                PrefUtils.with(this).remove("message_list_key_saved_at")
                //val message = db.getMessageListDao().getMessageList()
                Coroutines.main {
                    try {
                        val getMessage = viewModel.getMessageList(userId!!)
                        for (mess in getMessage) {
                            if ((mess.alici_user.user_id == userId && mess.gonderen_user.user_id == post_sahibi_id) ||
                                mess.alici_user.user_id == post_sahibi_id && mess.gonderen_user.user_id == userId
                            ) {
                                if (userId == mess.gonderen_user.user_id) {
                                    viewModel.updateMessageList(
                                        mess.messageId!!,
                                        mess.alici_user.user_id!!,
                                        mess.gonderen_user.user_id!!,
                                        userMessage,
                                        mess.alici_new_message_count + 1,
                                        mess.gonderen_new_message_count,
                                        0,
                                        0
                                    )
                                    viewModel.updateLocalMessageList(
                                        currentDate,
                                        userMessage,
                                        mess.messageId,
                                        mess.alici_new_message_count + 1,
                                        mess.gonderen_new_message_count
                                    )
                                    viewModel.pushNotification(
                                        mess.gonderen_user.user_name!!,
                                        mess.alici_user.user_name!!,
                                        userMessage,
                                        2
                                    )
                                    flag = true
                                } else if (userId == mess.alici_user.user_id) {
                                    viewModel.updateMessageList(
                                        mess.messageId!!,
                                        mess.gonderen_user.user_id!!,
                                        mess.alici_user.user_id!!,
                                        userMessage,
                                        mess.alici_new_message_count,
                                        mess.gonderen_new_message_count + 1,
                                        0,
                                        0
                                    )
                                    viewModel.updateLocalMessageList(
                                        currentDate,
                                        userMessage,
                                        mess.messageId,
                                        mess.alici_new_message_count,
                                        mess.gonderen_new_message_count + 1
                                    )
                                    viewModel.pushNotification(
                                        mess.alici_user.user_name!!,
                                        mess.gonderen_user.user_name!!,
                                        userMessage,
                                        2
                                    )
                                    flag = true
                                }
                            }
                        }
                        if (!flag) {
                            viewModel.saveMessageList(userId!!, post_sahibi_id!!, userMessage, 1, 0)
                            flag = true
                            viewModel.saveLocalMessageList(
                                MessageList(
                                    message = userMessage,
                                    tarih = currentDate,
                                    alici_new_message_count = 1,
                                    gonderen_new_message_count = 0,
                                    gonderen_user = User(user_id = userId),
                                    alici_user = User(
                                        user_id = post_sahibi_id,
                                        paths = photo,
                                        first_name = alici_name,
                                        user_name = aliciUserName,
                                        is_social_account = isSocial
                                    )
                                )
                            )
                        }
                    } catch (e: ApiException) {
                        toast("Sunucudan yanıt alınamıyor")
                    } catch (e: NoInternetException) {
                        toast("İnternet bağlantınızı kontrol ediniz")
                    } catch (e: java.lang.Exception) {
                        toast("Bir hata oluştu: " + e.message!!)
                    }
                }
            }
            etMesaj.setText("")
        }
        mesajlasmaBack.setOnClickListener { finish() }
        /*ivMesajlasmaPhoto.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("different_user_id",post_sahibi_id!!)
            bundle.putString("different_user_name",aliciUserName)
            bundle.putString("different_first_name",alici_name)
            bundle.putString("different_last_name",alici_name)
            bundle.putString("different_user_photo",currentUserPhoto)
            bundle.putInt("different_user_isSocial",currentUserIsSocial)
            PrefUtils.with(this).save("different_user",post_sahibi_id!!)

            val fragment = ProfileFragment()
            fragment.arguments = bundle
            val fragmentTransaction = supportFragmentManager.beginTransaction()
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.nav_host_fragment, fragment)
            fragmentTransaction.commit()
        }*/

        //listen firebase database
        try {
            chat_progress_bar.visibility = View.VISIBLE
            adapter = ChatAdapter(this, chatList)
            recyclerView = recyclerMesajlasma

            references = FirebaseDatabase.getInstance().getReference("Chats")
            references!!.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(p0: DataSnapshot) {
                    PrefUtils.with(this@ChatActivity).remove("message_list_key_saved_at")
                    when {
                        p0.hasChild("$userId-$post_sahibi_id") -> {
                            readChatList(p0.child("$userId-$post_sahibi_id"))
                        }
                        p0.hasChild("$post_sahibi_id-$userId") -> {
                            readChatList(p0.child("$post_sahibi_id-$userId"))
                        }
                    }
                }

                override fun onCancelled(p0: DatabaseError) {
                    Log.d("Log", "Cancel")
                }
            })
            recyclerView.also {
                it!!.layoutManager = LinearLayoutManager(this@ChatActivity)
                it.setHasFixedSize(true)
                it.adapter = adapter
                it.smoothScrollToPosition((it.adapter as ChatAdapter).itemCount)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun View.disable() {
        background.setColorFilter(
            resources.getColor(R.color.orangeTransparent),
            PorterDuff.Mode.MULTIPLY
        )
        isClickable = false
    }

    fun View.enable() {
        background.colorFilter = null
        isClickable = true
    }

    override fun onPause() {
        super.onPause()
        if (isFinishing) {
            Coroutines.main {
                viewModel.updateIsSeeMessage(userId!!, 0)
            }
        }
    }

    private fun updateNewMessageBadges(db: AppDatabase, userId: Int) {
        var exitFlag = false
        try {
            val message = db.getMessageListDao().getMessageList()
            message.observe(this, androidx.lifecycle.Observer { messages ->
                if (!exitFlag) {
                    for (mess in messages) {
                        exitFlag = if (userId != mess.gonderen_user.user_id) {
                            viewModel.updateMessageList(
                                messageId!!,
                                mess.gonderen_user.user_id!!,
                                mess.alici_user.user_id!!,
                                "",
                                0,
                                mess.gonderen_new_message_count,
                                1,
                                0
                            )
                            viewModel.updateLocalMessageBadges(
                                messageId!!,
                                0,
                                mess.gonderen_new_message_count
                            )
                            true
                        } else {
                            viewModel.updateMessageList(
                                messageId!!,
                                mess.alici_user.user_id!!,
                                mess.gonderen_user.user_id,
                                "",
                                mess.alici_new_message_count,
                                0,
                                0,
                                1
                            )
                            viewModel.updateLocalMessageBadges(
                                messageId!!,
                                mess.alici_new_message_count,
                                0
                            )
                            true
                        }
                    }
                }
            })
        }catch (e: ApiException) {
            Log.d("CHATT_1",e.message!!)
        } catch (e: NoInternetException) {
            Log.d("CHATT_2",e.message!!)
        } catch (e: Exception) {
            Log.d("CHATT_3", e.message!!)
        }

    }

    //Burası repository taşınacak
    fun readChatList(snapshot: DataSnapshot) {
        if (!chatList.isNullOrEmpty()) {
            chatList.clear()
            chat_progress_bar.visibility = View.INVISIBLE
        }
        try {
            chat_progress_bar.visibility = View.INVISIBLE
            for (data in snapshot.children) {
                val chat: Chat = data.getValue(Chat::class.java)!!
                chatList.add(chat)
                adapter!!.notifyDataSetChanged()
                recyclerView!!.smoothScrollToPosition((recyclerView!!.adapter as ChatAdapter).itemCount)
            }
        } catch (e: Exception) {
            Log.d("ERRR: ", e.message!!)
        }
    }

    override fun onStarted() {
        Log.d("STARTED","STARTED")
    }

    override fun onSuccess(message: String) {
        Log.d("SUCCESS",message)
    }

    override fun onFailure(message: String) {
        this.toast(message)
    }
}