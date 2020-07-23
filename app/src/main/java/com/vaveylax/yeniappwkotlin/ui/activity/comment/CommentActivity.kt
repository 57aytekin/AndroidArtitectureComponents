package com.vaveylax.yeniappwkotlin.ui.activity.comment

import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.vaveylax.yeniappwkotlin.R
import com.vaveylax.yeniappwkotlin.data.db.entities.Comment
import com.vaveylax.yeniappwkotlin.data.network.MyApi
import com.vaveylax.yeniappwkotlin.data.network.NetworkConnectionInterceptor
import com.vaveylax.yeniappwkotlin.data.network.repositories.CommentRepository
import com.vaveylax.yeniappwkotlin.databinding.CommentRowItem2Binding
import com.vaveylax.yeniappwkotlin.util.*
import kotlinx.android.synthetic.main.activity_commentt.*
import kotlinx.coroutines.launch

class CommentActivity : AppCompatActivity(), CommentListener, CommentRecyclerViewItemClick {
    private lateinit var viewModel: CommentViewModel
    var postId: Int? = null
    var postUserId: Int? = null
    var userName : String? = null
    var postUserName : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_commentt)

        postId = intent.getIntExtra("post_id", 1)
        postUserId = intent.getIntExtra("post_user_id", 0)
        postUserName = intent.getStringExtra("post_name")
        val userPhoto = PrefUtils.with(this).getString("user_image","")
        val isSocial = PrefUtils.with(this).getInt("is_social_account",0)
        userName = PrefUtils.with(this).getString("user_name","")
        //Post information
        val postUserPath = intent.getStringExtra("path")
        val postDate = intent.getStringExtra("date")
        val sharePost = intent.getStringExtra("share_post")

        val boldType = "$postUserName $sharePost"
        val sb: SpannableStringBuilder? = SpannableStringBuilder(boldType)
        val bss = StyleSpan(Typeface.BOLD)
        sb!!.setSpan(bss, 0, postUserName!!.length, Spannable.SPAN_EXCLUSIVE_INCLUSIVE)

        loadImage(iv_comment_photo, userPhoto, isSocial)
        /*Load Clicked Post*/
        loadImage(iv_comment_current_user_photo,postUserPath,0)
        tv_comment_current_user_name.text = sb
        tv_comment_current_user_date.text = postDate
        //
        val networkConnectionInterceptor = NetworkConnectionInterceptor(this)
        val api = MyApi(networkConnectionInterceptor)
        val repository = CommentRepository(api, postId!!)
        val factory = CommentViewModelFactory(repository)

        viewModel = ViewModelProviders.of(this, factory).get(CommentViewModel::class.java)

        viewModel.getComments()
        val userId = PrefUtils.with(this).getInt("user_id", 0)

        btnSend.setOnClickListener {
            val etMessage = etCommentMessage.text.toString().trim()
            if (etMessage.isEmpty()) {
                constrainLayout.snackbar("Lütfen paylaşımınızı giriniz.")
            } else {
                viewModel.onSaveCommentClick(userId, postId!!, etMessage)
                viewModel.pushNotification(postUserName!!, userName!!, etMessage,1)
            }
        }

        viewModel.commentListener = this

        comment_progress_bar.show()
        viewModel.comments.observe(this, Observer { comments ->
            if (comments.isEmpty()){
                comment_progress_bar.hide()
                tvBosComment.visibility = View.VISIBLE
                tvBosComment.text = "Bu gönderiye henüz yorum yapılmadı. İlk yorumu yapan sen ol."
            }else{
                tvBosComment.visibility = View.INVISIBLE
                comment_progress_bar.hide()
                swipe.isRefreshing = false
                recyclerviewComment.also {
                    it.layoutManager = LinearLayoutManager(this)
                    it.setHasFixedSize(true)
                    it.adapter = CommentActivityAdapter(comments, userId, postUserId!!,this)
                }
            }

        })
        swipe.setOnRefreshListener { viewModel.getComments() }
        ivBackButton.setOnClickListener { finish() }
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
                            viewModel.saveLikes(postUserId!!, comment.user_id!!, comment.id!!, postId!!)
                            viewModel.updateCommentLike(comment.id, 1)
                            viewModel.pushNotification(userName!!, comment.user_name!!,comment.comment!!,0 )
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
