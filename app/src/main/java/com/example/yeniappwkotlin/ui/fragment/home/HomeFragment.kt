package com.example.yeniappwkotlin.ui.fragment.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yeniappwkotlin.R
import com.example.yeniappwkotlin.data.db.database.AppDatabase
import com.example.yeniappwkotlin.data.db.entities.Post
import com.example.yeniappwkotlin.data.network.MyApi
import com.example.yeniappwkotlin.data.network.NetworkConnectionInterceptor
import com.example.yeniappwkotlin.data.network.NoConnectionInterceptor
import com.example.yeniappwkotlin.data.network.repositories.PostRepository
import com.example.yeniappwkotlin.databinding.FragmentHomeRowItemBinding
import com.example.yeniappwkotlin.ui.activity.comment.CommentActivity
import com.example.yeniappwkotlin.ui.activity.comment.CommentListener
import com.example.yeniappwkotlin.util.*
import kotlinx.android.synthetic.main.fragment_home_row_item.*
import kotlinx.android.synthetic.main.fragment_home_row_item.view.*
import kotlinx.android.synthetic.main.home_fragment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.internal.notify
import java.lang.Exception


class HomeFragment : Fragment(), RecyclerViewClickListener, CommentListener {

    var navController: NavController? = null

    companion object {
        fun newInstance() = HomeFragment()
    }

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.home_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val userId = PrefUtils.with(requireContext()).getInt("user_id", 0)
        val isSocial = PrefUtils.with(requireContext()).getInt("is_social_account", 0)
        val networkConnectionInterceptor = NetworkConnectionInterceptor(requireContext())
        val api = MyApi(networkConnectionInterceptor)
        val db = AppDatabase(requireContext())
        val repository = PostRepository(api, db, requireContext())
        val factory = HomeViewModelFactory(repository, userId)

        viewModel = ViewModelProviders.of(this, factory).get(HomeViewModel::class.java)

        progress_bar.show()
        Coroutines.main {
            try {
                val posts = viewModel.getPost.await()
                posts.observe(viewLifecycleOwner, Observer { post ->
                    progress_bar.hide()
                    recycler_home.also {
                        onRefresh.isRefreshing = false
                        it.layoutManager = LinearLayoutManager(requireContext())
                        it.setHasFixedSize(true)
                        it.adapter = HomeFragmentAdapter(post, isSocial,this)
                    }
                })
            }catch (e : Exception){
                val posts = viewModel.getLocalPost.await()
                posts.observe(viewLifecycleOwner, Observer { post ->
                    progress_bar.hide()
                    recycler_home.also {
                        onRefresh.isRefreshing = false
                        it.layoutManager = LinearLayoutManager(requireContext())
                        it.setHasFixedSize(true)
                        it.adapter = HomeFragmentAdapter(post, isSocial,this)
                    }
                })
            }
        }
    }

    override fun onRecyclerViewItemClick(
        view: View,
        post: Post,
        homeRowItemBinding: FragmentHomeRowItemBinding
    ) {
        when (view.id) {
            R.id.postBtnComment -> {
                val intent = Intent(requireContext(), CommentActivity::class.java)
                intent.putExtra("post_id", post.post_id)
                intent.putExtra("post_user_id", post.user_id)
                intent.putExtra("path", post.paths)
                startActivity(intent)
            }
            R.id.post_comment_count -> {
                val intent = Intent(requireContext(), CommentActivity::class.java)
                intent.putExtra("post_id", post.post_id)
                startActivity(intent)
            }
            R.id.home_likes -> {
                val userId = PrefUtils.with(requireContext()).getInt("user_id", 0)
                val likeCount : Int
                if (post.user_post_likes?.begeni_durum != null && post.user_post_likes.begeni_durum == 1 ){
                    likeCount = post.like_count!! -1
                    viewModel.btnPostLike(post.post_id!!, userId, post.like_count, 0)
                    homeRowItemBinding.homeLikes.setImageResource(R.drawable.icon_heart_gray)
                    homeRowItemBinding.postLikeCount.text = likeCount.toString()


                }else if(post.user_post_likes?.begeni_durum != null && post.user_post_likes.begeni_durum == 0){
                    likeCount = post.like_count!! +1
                    viewModel.btnPostLike(post.post_id!!, userId, post.like_count, 1)
                    homeRowItemBinding.homeLikes.setImageResource(R.drawable.icon_heart)
                    homeRowItemBinding.postLikeCount.text = likeCount.toString()
                }else {
                    likeCount = post.like_count!! +1
                    viewModel.saveUserPostLikes(userId, post.post_id!!, 1, likeCount)
                    homeRowItemBinding.homeLikes.setImageResource(R.drawable.icon_heart)
                    homeRowItemBinding.postLikeCount.text = likeCount.toString()
                }
            }
        }
    }


    override fun onRecyclerViewCheckUnckeck(
        view: View,
        post: Post,
        isChecked: Boolean,
        homeRowItemBinding: FragmentHomeRowItemBinding
    ) {
        when (view.id) {
            R.id.home_likes -> {

            }
            /*R.id.post_btn_like -> {
                val userId = PrefUtils.with(requireContext()).getInt("user_id", 0)
                var likeCount: Int?
                if (isChecked) {
                    likeCount = post.like_count!! + 1
                    if ((post.user_post_likes?.begeni_durum == 0) || (post.user_post_likes?.begeni_durum == 1)) {
                        viewModel.btnPostLike(post.post_id!!, userId, post.like_count, 1)
                    } else {
                        viewModel.saveUserPostLikes(userId, post.post_id!!, 1)
                    }
                    //post_like_count.text = "$likeCount Beğeni"
                } else {
                    likeCount = post.like_count!!
                    viewModel.btnPostLike(post.post_id!!, userId, post.like_count - 1, 0)
                }
                homeRowItemBinding.postLikeCount.text = "$likeCount Beğeni"
            }*/
        }
    }

    override fun onStarted() {

    }

    override fun onSuccess(message: String) {
        home_layout.snackbar(message)
    }

    override fun onFailure(message: String) {
        home_layout.snackbar(message)
    }
}
