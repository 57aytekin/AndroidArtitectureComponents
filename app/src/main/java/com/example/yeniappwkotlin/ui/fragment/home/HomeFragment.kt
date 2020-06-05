package com.example.yeniappwkotlin.ui.fragment.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yeniappwkotlin.R
import com.example.yeniappwkotlin.data.db.entities.Post
import com.example.yeniappwkotlin.data.network.MyApi
import com.example.yeniappwkotlin.data.network.NetworkConnectionInterceptor
import com.example.yeniappwkotlin.data.network.repositories.PostRepository
import com.example.yeniappwkotlin.ui.activity.comment.CommentActivity
import com.example.yeniappwkotlin.ui.activity.comment.CommentListener
import com.example.yeniappwkotlin.util.hide
import com.example.yeniappwkotlin.util.show
import com.example.yeniappwkotlin.util.snackbar
import kotlinx.android.synthetic.main.fragment_home_row_item.*
import kotlinx.android.synthetic.main.home_fragment.*


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

        val networkConnectionInterceptor  = NetworkConnectionInterceptor(requireContext())
        val api = MyApi(networkConnectionInterceptor)
        val repository = PostRepository(api)
        val factory = HomeViewModelFactory(repository)

        viewModel = ViewModelProviders.of(this, factory).get(HomeViewModel::class.java)
        viewModel.getPosts()
        progress_bar.show()
        viewModel.posts.observe(viewLifecycleOwner, Observer {posts ->
            progress_bar.hide()
            recycler_home.also {
                onRefresh.isRefreshing = false
                it.layoutManager = LinearLayoutManager(requireContext())
                it.setHasFixedSize(true)
                it.adapter = HomeFragmentAdapter(posts, this)
            }
        })

        onRefresh.setOnRefreshListener { viewModel.getPosts() }
    }

    override fun onRecyclerViewItemClick(view: View, post: Post) {
        when(view.id){
            R.id.post_btn_comment  -> {
                val intent = Intent(requireContext(), CommentActivity::class.java)
                intent.putExtra("post_id",post.id)
                startActivity(intent)
            }
            R.id.post_comment_count -> {
                val intent = Intent(requireContext(), CommentActivity::class.java)
                intent.putExtra("post_id",post.id)
                startActivity(intent)
            }
            R.id.post_btn_like -> {
                viewModel.btnPostLike(post.like_count!!, post.id!!)
                var builder : StringBuilder = StringBuilder()
                builder.append(post.like_count)
                builder.append(" Beğeni")
                post_like_count.text = builder.toString()
            }
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
