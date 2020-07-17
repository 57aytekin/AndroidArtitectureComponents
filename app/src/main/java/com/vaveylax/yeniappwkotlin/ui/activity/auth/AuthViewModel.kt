package com.vaveylax.yeniappwkotlin.ui.activity.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import com.vaveylax.yeniappwkotlin.data.db.entities.User
import com.vaveylax.yeniappwkotlin.data.network.repositories.UserRepository
import com.vaveylax.yeniappwkotlin.util.ApiException
import com.vaveylax.yeniappwkotlin.util.Coroutines
import com.vaveylax.yeniappwkotlin.util.NoInternetException
import java.lang.Exception

class AuthViewModel(
    private val repository: UserRepository
) : ViewModel() {

    fun getLoggedInUser() = repository.getUser()

    suspend fun userLogin(
        email : String, password : String
    ) = repository.userLogin(email, password)

    suspend fun updateUserLoginStatu(
        user_id : Int, is_login : Int
    ) = repository.updateUserLoginStatu(user_id, is_login)

    fun updateTokenIsLoginLastLogin(
        user_id: Int, token: String, is_login: Int
    ) {
        Coroutines.main {
            try {
                repository.updateTokenIsLoginLastLogin(user_id, token, is_login)
            } catch (e: ApiException) {
                Log.d("AUTH_API_ERROR: ",e.message!!)
            } catch (e: NoInternetException) {
                Log.d("AUTH_NET_ERROR: ",e.message!!)
            } catch (e : Exception){
                Log.d("AUTH_EXP: ",e.printStackTrace().toString())
            }
        }
    }

    suspend fun userRegister(
        userName : String, firsName : String,lastName : String, email: String, phone: String, password: String, paths: String, is_social_account : Int
    ) = repository.userRegister(userName, firsName,lastName, email, phone, password, paths, is_social_account)

    suspend fun saveLoggedInUser(user: User) = repository.saveUser(user)
}