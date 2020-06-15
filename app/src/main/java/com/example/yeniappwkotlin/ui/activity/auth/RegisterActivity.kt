package com.example.yeniappwkotlin.ui.activity.auth

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.yeniappwkotlin.R
import com.example.yeniappwkotlin.data.db.database.AppDatabase
import com.example.yeniappwkotlin.data.network.MyApi
import com.example.yeniappwkotlin.data.network.NetworkConnectionInterceptor
import com.example.yeniappwkotlin.data.network.NoConnectionInterceptor
import com.example.yeniappwkotlin.data.network.repositories.UserRepository
import com.example.yeniappwkotlin.databinding.ActivityRegisterBinding
import com.example.yeniappwkotlin.ui.activity.home.MainActivity
import com.example.yeniappwkotlin.util.ApiException
import com.example.yeniappwkotlin.util.NoInternetException
import com.example.yeniappwkotlin.util.PrefUtils
import com.example.yeniappwkotlin.util.snackbar
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding : ActivityRegisterBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val networkConnectionInterceptor = NetworkConnectionInterceptor(this)
        val api = MyApi(networkConnectionInterceptor)
        val db = AppDatabase(this)
        val repository = UserRepository(api, db)
        val factory = AuthViewModelFactory(repository)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        viewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)

        //binding.register = viewModel

        viewModel.getLoggedInUser().observe(this, Observer { user ->
            if(user != null){
                PrefUtils.with(this).save("user_id",user.user_id!!)
                Intent(this, MainActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                }
            }
        })
        binding.btnRegister.setOnClickListener {
            userRegister()
        }
    }

    private fun userRegister(){
        val name = binding.etRegisterName.text.toString().trim()
        val email = binding.etRegisterEmail.text.toString().trim()
        val password = binding.etRegisterPassword.text.toString().trim()
        val password2 = binding.etRegisterPassword2.text.toString().trim()
        if(name.isEmpty()){
            binding.root.snackbar("Lütfen kullanıcı adınızı giriniz")
            return
        }
        if (email.isEmpty()){
            binding.root.snackbar("Lütfen email adresinizi giriniz")
            return
        }
        if (password.isEmpty() ||password2.isEmpty()){
            binding.root.snackbar("Lütfen şifrenizi giriniz")
            return
        }
        if (password != password2){
            binding.root.snackbar("Şifreleriniz uyuşmamakta")
            return
        }
        lifecycleScope.launch {
            try {
                val authResponse = viewModel.userRegister(name, email, password)
                if (authResponse.login != null){
                    viewModel.saveLoggedInUser(authResponse.login)
                }else{
                    binding.root.snackbar(authResponse.message!!)
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }

}
