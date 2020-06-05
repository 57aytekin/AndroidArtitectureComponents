package com.example.yeniappwkotlin.ui.activity.comment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yeniappwkotlin.R
import com.example.yeniappwkotlin.data.network.MyApi
import com.example.yeniappwkotlin.data.network.NetworkConnectionInterceptor
import com.example.yeniappwkotlin.data.network.repositories.CommentRepository
import com.example.yeniappwkotlin.databinding.ActivityCommenttBinding
import com.example.yeniappwkotlin.util.snackbar
import kotlinx.android.synthetic.main.activity_commentt.*

class CommentActivity : AppCompatActivity(), CommentListener {
    private lateinit var viewModel: CommentViewModel
    var postId : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        postId = intent.getIntExtra("post_id",1)
        val networkConnectionInterceptor  = NetworkConnectionInterceptor(this)
        val api = MyApi(networkConnectionInterceptor)
        val repository = CommentRepository(api, postId!!)
        val factory = CommentViewModelFactory(repository,4, postId)

        val binding : ActivityCommenttBinding = DataBindingUtil.setContentView(this, R.layout.activity_commentt)
        viewModel = ViewModelProviders.of(this, factory).get(CommentViewModel::class.java)
        viewModel.getComments()
        binding.comments = viewModel

        viewModel.commentListener = this

        viewModel.comments.observe(this, Observer { comments ->
            swipe.isRefreshing = false
            recyclerviewComment.also {
                it.layoutManager = LinearLayoutManager(this)
                it.setHasFixedSize(true)
                it.adapter = CommentActivityAdapter(comments, postId!!)
            }
        })
        swipe.setOnRefreshListener { viewModel.getComments() }
    }

    override fun onStarted() {
    }

    override fun onSuccess(message: String) {
        etCommentMessage.setText("")
        viewModel.getComments()
    }

    override fun onFailure(message: String) {
        constrainLayout.snackbar(message)
    }
}
