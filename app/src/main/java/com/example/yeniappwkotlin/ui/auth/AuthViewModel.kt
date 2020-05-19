package com.example.yeniappwkotlin.ui.auth

import android.content.Intent
import android.view.View
import androidx.lifecycle.ViewModel
import com.example.yeniappwkotlin.data.network.repositories.UserRepository
import com.example.yeniappwkotlin.util.ApiException
import com.example.yeniappwkotlin.util.Coroutines
import com.example.yeniappwkotlin.util.NoInternetException

class AuthViewModel(
    private val repository: UserRepository
) : ViewModel() {
    var email: String? = null
    var name : String? = null
    var password: String? = null
    var passwordConfirm : String? = null

    var authListener: AuthListener? = null

    fun onLoginButtonClick(view: View) {
        authListener?.onStarted()

        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            authListener?.onFailure("Geçersiz email veya şifre")
            return
        }
        Coroutines.main {
            try {
                val authResponse = repository.userLogin(email!!, password!!)
                authResponse.login?.let {
                    authListener?.onSuccess(it)
                    return@main
                }
                authListener?.onFailure(authResponse.message!!)
            } catch (e: ApiException) {
                authListener?.onFailure(e.message!!)
            }catch (e : NoInternetException){
                authListener?.onFailure(e.message!!)
            }
        }
    }

    fun onRegisterButtonClick(view: View){
        authListener?.onStarted()
        if (name.isNullOrEmpty()){
            authListener?.onFailure("Lütfen kullanıcı adınızı giriniz!")
            return
        }
        if (email.isNullOrEmpty()){
            authListener?.onFailure("Lütfen e-mail adresinizi giriniz!")
            return
        }
        if (password.isNullOrEmpty()){
            authListener?.onFailure("Lütfen şifrenizi giriniz")
            return
        }
        if (passwordConfirm.isNullOrEmpty()){
            authListener?.onFailure("Lütfen şifrenizi tekrar giriniz")
            return
        }
        if (password != passwordConfirm){
            authListener?.onFailure("Şifreleriniz uyuşmamaıkta, Lütfen kontrol ediniz")
            return
        }
        Coroutines.main {
            try {
                val authResponse = repository.userRegister(name!!, email!!, password!!)
                authResponse.login?.let {
                    authListener?.onSuccess(it)
                    return@main
                }
                authListener?.onFailure(authResponse.message!!)
            }catch (e : ApiException){
                authListener?.onFailure(e.message!!)
            }catch (e : NoInternetException){
                authListener?.onFailure(e.message!!)
            }
        }
    }

    fun onRegister(view : View){
        Intent(view.context, RegisterActivity::class.java).also{
            view.context.startActivity(it)
        }
    }
}