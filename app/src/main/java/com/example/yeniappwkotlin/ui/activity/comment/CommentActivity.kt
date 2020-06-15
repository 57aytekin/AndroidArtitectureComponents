package com.example.yeniappwkotlin.ui.activity.comment

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yeniappwkotlin.R
import com.example.yeniappwkotlin.data.db.entities.Comment
import com.example.yeniappwkotlin.data.network.MyApi
import com.example.yeniappwkotlin.data.network.NetworkConnectionInterceptor
import com.example.yeniappwkotlin.data.network.NoConnectionInterceptor
import com.example.yeniappwkotlin.data.network.repositories.CommentRepository
import com.example.yeniappwkotlin.databinding.ActivityCommenttBinding
import com.example.yeniappwkotlin.databinding.CommentRowItem2Binding
import com.example.yeniappwkotlin.util.ApiException
import com.example.yeniappwkotlin.util.PrefUtils
import com.example.yeniappwkotlin.util.snackbar
import kotlinx.android.synthetic.main.activity_commentt.*
import kotlinx.coroutines.launch

class CommentActivity : AppCompatActivity(), CommentListener, CommentRecyclerViewItemClick {
    private lateinit var viewModel: CommentViewModel
    var postId: Int? = null
    var postUserId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        postId = intent.getIntExtra("post_id", 1)
        postUserId = intent.getIntExtra("post_user_id", 0)
        val networkConnectionInterceptor = NetworkConnectionInterceptor(this)
        val api = MyApi(networkConnectionInterceptor)
        val repository = CommentRepository(api, postId!!)
        val factory = CommentViewModelFactory(repository)

        val binding: ActivityCommenttBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_commentt)
        viewModel = ViewModelProviders.of(this, factory).get(CommentViewModel::class.java)
        viewModel.getComments()
        val userId = PrefUtils.with(this).getInt("user_id", 0)

        binding.btnSend.setOnClickListener {
            val etMessage = binding.etCommentMessage.text.toString().trim()
            if (etMessage.isEmpty()) {
                binding.constrainLayout.snackbar("Lütfen paylaşımınızı giriniz.")
            } else {
                viewModel.onSaveCommentClick(userId, postId!!, etMessage)
            }
        }

        viewModel.commentListener = this

        viewModel.comments.observe(this, Observer { comments ->
            swipe.isRefreshing = false
            recyclerviewComment.also {
                it.layoutManager = LinearLayoutManager(this)
                it.setHasFixedSize(true)
                it.adapter = CommentActivityAdapter(comments, userId, postUserId!!, this)
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

    override fun onRecyclerViewCheckUncheck(
        view: View,
        comment: Comment,
        isChecked: Boolean,
        commentRowItemBinding: CommentRowItem2Binding
    ) {
        when (view.id) {
            R.id.toggle -> {
                lifecycleScope.launch {
                    try {
                        if (isChecked) {
                            commentRowItemBinding.toggle.setBackgroundResource(R.drawable.ic_favorite_orange)
                            viewModel.saveLikes(postUserId!!, comment.user_id!!, comment.id!!)
                            viewModel.updateCommentLike(comment.id, 1)
                        } else {
                            commentRowItemBinding.toggle.setBackgroundResource(R.drawable.ic_favorite_black_24dp)
                            viewModel.updateCommentLike(comment.id!!, 0)
                        }
                    } catch (e: Exception) {
                        commentRowItemBinding.root.snackbar(e.message!!)
                    }
                }
            }
        }
    }
}
