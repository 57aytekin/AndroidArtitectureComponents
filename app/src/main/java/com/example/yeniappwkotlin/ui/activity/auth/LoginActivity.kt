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
import com.example.yeniappwkotlin.data.network.repositories.UserRepository
import com.example.yeniappwkotlin.databinding.ActivityLoginBinding
import com.example.yeniappwkotlin.ui.activity.home.MainActivity
import com.example.yeniappwkotlin.util.ApiException
import com.example.yeniappwkotlin.util.NoInternetException
import com.example.yeniappwkotlin.util.PrefUtils
import com.example.yeniappwkotlin.util.snackbar
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val networkConnectionInterceptor = NetworkConnectionInterceptor(this)
        val api = MyApi(networkConnectionInterceptor)
        val db = AppDatabase(this)
        val repository = UserRepository(api, db)
        val factory =
            AuthViewModelFactory(repository)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        viewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)


        viewModel.getLoggedInUser().observe(this, Observer { user ->
            if (user != null) {
                PrefUtils.with(this).save("user_id",user.user_id!!)
                Intent(this, MainActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(it)
                }
            }
        })

        binding.btnLogin.setOnClickListener {
            loginUser()
        }
        binding.textViewLoginRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser() {
        val email = binding.etLoginEmail.text.toString().trim()
        val password = binding.etLoginPassword.text.toString().trim()
        lifecycleScope.launch {
            try {
                val authResponse = viewModel.userLogin(email, password)

                if (authResponse.login != null){
                    viewModel.saveLoggedInUser(authResponse.login)
                }else{
                    binding.rootLayout.snackbar(authResponse.message!!)
                }
            }catch (e: ApiException){
                e.printStackTrace()
            }catch (e: NoInternetException){
                e.printStackTrace()
            }
        }
    }
}
