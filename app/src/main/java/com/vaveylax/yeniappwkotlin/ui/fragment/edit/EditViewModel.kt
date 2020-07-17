package com.vaveylax.yeniappwkotlin.ui.fragment.edit

import android.app.Activity
import androidx.lifecycle.*
import androidx.navigation.Navigation
import com.vaveylax.yeniappwkotlin.R
import com.vaveylax.yeniappwkotlin.data.network.repositories.UserRepository
import com.vaveylax.yeniappwkotlin.util.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class EditViewModel(
    private val activity: Activity,
    private val repository: UserRepository,
    val userId : Int
) : ViewModel() {
    var like_count : Int? = null
    var comment_count : Int? = null
    var editListener: EditListener? = null
    private val navController  = Navigation.findNavController(activity, R.id.nav_host_fragment)
    fun onSavePostButtonClick(share_post : String){
        like_count = 0
        comment_count = 0
        if (share_post.isEmpty()){
            editListener?.onFailure("Lütfen paylaşımınızı giriniz")
            return
        }
        editListener?.onStarted()
        Coroutines.main {
            try {
                val asd = activity.findViewById<BottomNavigationView>(R.id.bottomNavigation)
                val editResponse = repository.savePost(userId,share_post, like_count!!, comment_count!!)
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
                editListener?.onFailure("check_internet")
            }catch (e : Exception){
                e.printStackTrace()
                editListener?.onFailure(e.message!!)
            }
        }
    }
}
