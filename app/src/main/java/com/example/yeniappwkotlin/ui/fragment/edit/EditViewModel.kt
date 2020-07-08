package com.example.yeniappwkotlin.ui.fragment.edit

import android.app.Activity
import android.util.Log
import android.view.View
import androidx.lifecycle.*
import androidx.navigation.Navigation
import com.example.yeniappwkotlin.R
import com.example.yeniappwkotlin.data.db.entities.CURRENT_USER_ID
import com.example.yeniappwkotlin.data.db.entities.Post
import com.example.yeniappwkotlin.data.db.entities.User
import com.example.yeniappwkotlin.data.network.repositories.PostRepository
import com.example.yeniappwkotlin.data.network.repositories.UserRepository
import com.example.yeniappwkotlin.util.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Job

class EditViewModel(
    private val activity: Activity,
    private val repository: UserRepository,
    val userId : Int
) : ViewModel() {
    var user_id: Int? = null
    var share_post : String? = null
    var like_count : Int? = null
    var comment_count : Int? = null
    var editListener: EditListener? = null
    private val navController  = Navigation.findNavController(activity, R.id.nav_host_fragment)
    fun onSavePostButtonClick(view : View){
        like_count = 0
        comment_count = 0
        if (share_post.isNullOrEmpty()){
            editListener?.onFailure("Lütfen paylaşımınızı giriniz")
            return
        }
        editListener?.onStarted()
        Coroutines.main {
            try {
                val asd = activity.findViewById<BottomNavigationView>(R.id.bottomNavigation)
                val editResponse = repository.savePost(userId,share_post!!, like_count!!, comment_count!!)
                editResponse.message.let {
                    editListener?.onSuccess(it)
                    PrefUtils.with(activity.applicationContext).save("post_key_saved_at","")
                    navController.navigate(
                        R.id.action_editFragment_to_homeFragment )
                    asd.checkItem(R.id.nav_tab_home)
                    return@main
                }
            }catch (e : ApiException){
                editListener?.onFailure(e.message!!)
            }catch (e : NoInternetException){
                editListener?.onFailure(e.message!!)
            }catch (e : Exception){
                e.printStackTrace()
            }
        }
    }
}
