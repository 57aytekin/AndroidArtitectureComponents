package com.vaveylax.yeniappwkotlin.ui.fragment.profile_paylasimlar

import androidx.lifecycle.ViewModel
import com.vaveylax.yeniappwkotlin.data.db.entities.Post
import com.vaveylax.yeniappwkotlin.data.network.repositories.PostRepository
import com.vaveylax.yeniappwkotlin.ui.activity.comment.CommentListener
import com.vaveylax.yeniappwkotlin.util.lazyDeffered
import kotlinx.coroutines.Job

class ProfilePaylasimlarViewModel(
    val repository: PostRepository,
    val user_id : Int
) : ViewModel() {
    private lateinit var job : Job
    var commentListener : CommentListener? = null

    val getPost by lazyDeffered {
        repository.getUserPosts(user_id)
    }

    suspend fun getLocalUserPost(userId : Int) : List<Post>{
        return repository.getLocalUserPost(userId)
    }
}
