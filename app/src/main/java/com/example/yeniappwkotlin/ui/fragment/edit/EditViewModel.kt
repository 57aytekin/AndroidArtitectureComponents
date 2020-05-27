package com.example.yeniappwkotlin.ui.fragment.edit

import android.app.Activity
import android.util.Log
import android.view.View
import androidx.lifecycle.*
import androidx.navigation.Navigation
import com.example.yeniappwkotlin.R
import com.example.yeniappwkotlin.data.db.entities.CURRENT_USER_ID
import com.example.yeniappwkotlin.data.db.entities.User
import com.example.yeniappwkotlin.data.network.repositories.PostRepository
import com.example.yeniappwkotlin.data.network.repositories.UserRepository
import com.example.yeniappwkotlin.util.ApiException
import com.example.yeniappwkotlin.util.Coroutines
import com.example.yeniappwkotlin.util.NoInternetException
import kotlinx.coroutines.Job

class EditViewModel(
    private val activity: Activity,
    private val repository: UserRepository
) : ViewModel() {
    var user_id: Int? = null
    var share_post : String? = null
    var like_count : Int? = null
    var user = repository.getUser()
    var comment_count : Int? = null
    var editListener: EditListener? = null
    private val navController  = Navigation.findNavController(activity, R.id.nav_host_fragment)
    fun onSavePostButtonClick(view : View){
        like_count = 0
        comment_count = 0
        user_id = user.value?.user_id
        if (share_post.isNullOrEmpty()){
            editListener?.onFailure("Lütfen paylaşımınızı giriniz")
            return
        }
        editListener?.onStarted()
        Coroutines.main {
            try {
                val editResponse = repository.savePost(user_id!!,share_post!!, like_count!!, comment_count!!)
                editResponse.message.let {
                    editListener?.onSuccess(it)
                    navController.navigate(
                        R.id.action_editFragment_to_homeFragment )
                    return@main
                }
            }catch (e : ApiException){
                editListener?.onFailure(e.message!!)
            }catch (e : NoInternetException){
                editListener?.onFailure(e.message!!)
            }
        }
    }

}
