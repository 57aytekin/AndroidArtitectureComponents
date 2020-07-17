package com.vaveylax.yeniappwkotlin.ui.fragment.message

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager

import com.vaveylax.yeniappwkotlin.R
import com.vaveylax.yeniappwkotlin.data.db.database.AppDatabase
import com.vaveylax.yeniappwkotlin.data.db.entities.MessageList
import com.vaveylax.yeniappwkotlin.data.network.MyApi
import com.vaveylax.yeniappwkotlin.data.network.NetworkConnectionInterceptor
import com.vaveylax.yeniappwkotlin.data.network.repositories.MessageListRepository
import com.vaveylax.yeniappwkotlin.ui.activity.chat.ChatActivity
import com.vaveylax.yeniappwkotlin.util.*
import kotlinx.android.synthetic.main.appbar_message.*
import kotlinx.android.synthetic.main.message_fragment.*

class MessageFragment : Fragment(), MessageClickListener {
    var navController: NavController? = null
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

        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        directsProfile(requireActivity(), navController!!, ivMessagePhoto)

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
                e.printStackTrace()
            } catch (e : NoInternetException){
                requireActivity().toast(getString(R.string.check_internet))
                e.printStackTrace()
            } catch (e : ApiException){
                e.printStackTrace()
            }
        }
    }

    override fun onRecyclerViewItemClick(view: View, message: MessageList) {
        val userId = PrefUtils.with(requireContext()).getInt("user_id",-1)
        when(view.id){
            R.id.message_list_container -> {
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
            R.id.messageItemUserPhoto -> {
                val bundle = Bundle().also {
                    if (userId == message.gonderen_user.user_id){
                        PrefUtils.with(requireContext()).save("different_user",message.alici_user.user_id!!)
                        it.putInt("different_user_id",message.alici_user.user_id)
                        it.putString("different_user_name",message.alici_user.user_name)
                        it.putString("different_first_name",message.alici_user.first_name)
                        it.putString("different_last_name",message.alici_user.last_name)
                        it.putString("different_user_photo",message.alici_user.paths)
                        it.putInt("different_user_isSocial",message.alici_user.is_social_account!!)
                    }else{
                        PrefUtils.with(requireContext()).save("different_user",message.gonderen_user.user_id!!)
                        it.putInt("different_user_id",message.gonderen_user.user_id)
                        it.putString("different_user_name",message.gonderen_user.user_name)
                        it.putString("different_first_name",message.gonderen_user.first_name)
                        it.putString("different_last_name",message.gonderen_user.last_name)
                        it.putString("different_user_photo",message.gonderen_user.paths)
                        it.putInt("different_user_isSocial",message.gonderen_user.is_social_account!!)
                    }
                }
                navController!!.navigate(R.id.profileFragment, bundle)
            }
        }

    }

}
