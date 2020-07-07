package com.example.yeniappwkotlin.ui.fragment.like

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.yeniappwkotlin.R
import com.example.yeniappwkotlin.data.db.database.AppDatabase
import com.example.yeniappwkotlin.data.db.entities.Likes
import com.example.yeniappwkotlin.data.network.MyApi
import com.example.yeniappwkotlin.data.network.NetworkConnectionInterceptor
import com.example.yeniappwkotlin.data.network.NoConnectionInterceptor
import com.example.yeniappwkotlin.data.network.repositories.LikesRepository
import com.example.yeniappwkotlin.ui.activity.chat.ChatActivity
import com.example.yeniappwkotlin.util.*
import kotlinx.android.synthetic.main.activity_commentt.*
import kotlinx.android.synthetic.main.appbar_likes.*
import kotlinx.android.synthetic.main.like_fragment.*
import kotlinx.coroutines.launch

class LikeFragment : Fragment(), ClickListener {
    var navController: NavController? = null

    companion object {
        fun newInstance() = LikeFragment()
    }

    private lateinit var viewModel: LikeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.like_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        openCloseSoftKeyboard(requireContext(),requireView(), false)

        val userId = PrefUtils.with(requireContext()).getInt("user_id", 0)
        val networkConnectionInterceptor = NetworkConnectionInterceptor(requireContext())
        val api = MyApi(networkConnectionInterceptor)
        val db = AppDatabase(requireContext())
        val repository = LikesRepository(api,db,requireContext())
        val factory = LikeViewModelFactory(repository, userId)

        viewModel = ViewModelProviders.of(this, factory).get(LikeViewModel::class.java)

        //Load Image
        val userPhoto = PrefUtils.with(requireContext()).getString("user_image","")
        val isSocial = PrefUtils.with(requireContext()).getInt("is_social_account",0)
        loadImage(ivLikePhoto, userPhoto, isSocial)

        like_progress_bar.show()
        Coroutines.main {
            try {
                val likes = viewModel.getLikes.await()
                likes.observe(viewLifecycleOwner, Observer { like ->
                    if (like.isEmpty()){
                        tvEmptyLike.visibility = View.VISIBLE
                        like_progress_bar.hide()
                    }else{
                        tvEmptyLike.visibility = View.INVISIBLE
                        like_progress_bar.hide()
                        likeFragmentRecycler.also {
                            it.layoutManager = LinearLayoutManager(requireContext())
                            it.setHasFixedSize(true)
                            it.adapter = LikeFragmentAdapter(like,this)
                        }
                    }
                })
            }catch (e : Exception){
                Log.d("LIKE_FRAGMENT",e.message!!)
            }
        }
    }

    override fun onRecyclerViewItemClick(view: View, likes: Likes) {
        Intent(requireContext(), ChatActivity::class.java).also {
            it.putExtra("photo",likes.paths)
            it.putExtra("alici_name", likes.first_name)
            it.putExtra("alici_username", likes.user_name)
            it.putExtra("post_sahibi_id", likes.post_sahibi_id)
            it.putExtra("is_social", likes.is_social_account)
            startActivity(it)
        }
    }
}
