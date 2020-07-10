package com.example.yeniappwkotlin.ui.fragment.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.yeniappwkotlin.data.db.database.AppDatabase
import com.example.yeniappwkotlin.data.db.entities.Post
import com.example.yeniappwkotlin.data.network.MyApi
import com.example.yeniappwkotlin.data.network.repositories.UserRepository
import com.example.yeniappwkotlin.util.ApiException
import com.example.yeniappwkotlin.util.NoInternetException
import java.lang.Exception

class ProfileViewModel(
    private val repository: UserRepository
) : ViewModel() {
    suspend fun updateIsLogin(user_id : Int, is_login : Int) {
        try {
            repository.updateIsLogin(user_id, is_login)
        } catch (e: ApiException) {
            Log.d("PROFILE_ERROR: ",e.message!!)
        } catch (e: NoInternetException) {
            Log.d("PROFILE_NET_ERROR: ",e.message!!)
        } catch (e : Exception){
            Log.d("PROFILE_EXP: ",e.printStackTrace().toString())
        }
    }

    suspend fun getLocalUserPost(userId : Int) : List<Post>{
        return repository.getLocalUserPost(userId)
    }

    suspend fun updateUserLoginStatu(
        user_id : Int, is_login : Int
    ){
        try {
            repository.updateUserLoginStatu(user_id, is_login)
        } catch (e: ApiException) {
            Log.d("AUTH_API_ERROR: ",e.message!!)
        } catch (e: NoInternetException) {
            Log.d("AUTH_NET_ERROR: ",e.message!!)
        } catch (e : Exception){
            Log.d("AUTH_EXP: ",e.printStackTrace().toString())
        }
    }

    fun deleteUser() = repository.deleteUser()
    fun deletePost() = repository.deletePost()
    fun deleteLikes() = repository.deleteLikes()
    fun deleteMessageList() = repository.deleteMessageList()
}
