package com.example.yeniappwkotlin.ui.activity.comment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yeniappwkotlin.R
import com.example.yeniappwkotlin.data.network.MyApi
import com.example.yeniappwkotlin.data.network.NetworkConnectionInterceptor
import com.example.yeniappwkotlin.data.network.repositories.CommentRepository
import kotlinx.android.synthetic.main.activity_commentt.*

class CommentActivity : AppCompatActivity() {
    private lateinit var viewModel: CommentViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_commentt)
        val postId = intent.getIntExtra("post_id",1)
        val networkConnectionInterceptor  = NetworkConnectionInterceptor(this)
        val api = MyApi(networkConnectionInterceptor)
        val repository = CommentRepository(api, postId)
        val factory = CommentViewModelFactory(repository)


        viewModel = ViewModelProviders.of(this, factory).get(CommentViewModel::class.java)
        viewModel.getComments()

        viewModel.comments.observe(this, Observer { comments ->
            recyclerviewComment.also {
                it.layoutManager = LinearLayoutManager(this)
                it.setHasFixedSize(true)
                it.adapter = CommentActivityAdapter(comments, postId)
            }
        })
    }
}
