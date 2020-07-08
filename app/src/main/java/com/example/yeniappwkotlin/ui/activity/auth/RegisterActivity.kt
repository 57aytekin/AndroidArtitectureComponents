package com.example.yeniappwkotlin.ui.activity.auth

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.util.Patterns
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
import com.example.yeniappwkotlin.ui.activity.PhoneAuthActivity
import com.example.yeniappwkotlin.ui.activity.home.MainActivity
import com.example.yeniappwkotlin.util.*
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.concurrent.TimeUnit

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: AuthViewModel
    private var userName: String? = null
    private var firstName: String? = null
    private var lastName: String? = null
    private var email: String? = null
    private var phone: String? = null
    private var password: String? = null
    private var password2: String? = null

    companion object {
        private const val PHONE_AUTH = 200
    }

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
            if (user != null) {
                PrefUtils.with(this).save("user_id", user.user_id!!)
                Intent(this, MainActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(it)
                }
            }
        })
        binding.btnRegister.setOnClickListener {
            userName = binding.etRegisterUserName.text.toString().trim()
            firstName = binding.etRegisterFirstName.text.toString().trim()
            lastName = binding.etRegisterLastName.text.toString().trim()
            email = binding.etRegisterEmail.text.toString().trim()
            phone = binding.etRegisterPhone.rawText.toString().trim()
            password = binding.etRegisterPassword.text.toString().trim()
            password2 = binding.etRegisterPassword2.text.toString().trim()

            if (checkValidation(
                    userName!!,
                    firstName!!,
                    lastName!!,
                    email!!,
                    phone!!,
                    password!!,
                    password2!!
                )
            ) {
                val intent = Intent(this, PhoneAuthActivity::class.java)
                intent.putExtra("phone_number", phone)
                startActivityForResult(intent, PHONE_AUTH)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PHONE_AUTH && resultCode == Activity.RESULT_OK) {
            userRegister(
                userName!!,
                firstName!!,
                lastName!!,
                email!!,
                phone!!,
                password!!,
                password2!!
            )
        }
    }

    private fun userRegister(
        userName: String,
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        password: String,
        password2: String
    ) {
        PrefUtils.with(this).save("is_first", false)
        lifecycleScope.launch {
            try {
                val authResponse = viewModel.userRegister(
                    userName,
                    firstName,
                    lastName,
                    email,
                    phone,
                    password,
                    "image/default.jpg",
                    0
                )
                if (authResponse.login != null) {
                    viewModel.saveLoggedInUser(authResponse.login)
                } else {
                    val message = authResponse.message
                    when {
                        message.equals("username_taken") -> {
                            toast(getString(R.string.username_taken))
                        }
                        message.equals("mail_taken") -> {
                            toast(getString(R.string.mail_taken))
                        }
                        message.equals("check_information") -> {
                            toast(getString(R.string.check_information))
                        }
                        else -> {
                            toast(message!!)
                        }
                    }
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            } catch (e : Exception){
                e.printStackTrace()
            }
        }
    }

    private fun checkValidation(
        userName: String,
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        password: String,
        password2: String
    ): Boolean {
        if (userName.isEmpty() || firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty() || password2.isEmpty()) {
            toast(getString(R.string.fill_fields))
            return false
        }
        if (userName.length < 5) {
            textInputSettings(binding.txtUserName, getString(R.string.valid_user_name))
            return false
        } else {
            binding.txtUserName.isErrorEnabled = false
        }

        if (firstName.length < 3) {
            textInputSettings(binding.txtInput1, getString(R.string.valid_first_name))
            return false
        } else {
            binding.txtInput1.isErrorEnabled = false
        }
        if (lastName.length < 2) {
            textInputSettings(binding.txtLastName, getString(R.string.valid_last_name))
            return false
        } else {
            binding.txtLastName.isErrorEnabled = false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            textInputSettings(binding.txtInput2, getString(R.string.valid_mail))
            return false
        } else {
            binding.txtInput2.isErrorEnabled = false
        }
        if (phone.length < 10) {
            textInputSettings(binding.txtPhone, getString(R.string.valid_phone))
            return false
        } else {
            binding.txtPhone.isErrorEnabled = false
        }
        if (password.length < 9) {
            textInputSettings(binding.txtInput3, getString(R.string.valid_password))
            return false
        } else {
            binding.txtInput3.isErrorEnabled = false
        }
        if (password != password2) {
            textInputSettings(binding.txtInput3, getString(R.string.match_password))
            return false
        } else {
            binding.txtInput3.isErrorEnabled = false
            return true
        }
    }

    private fun textInputSettings(txtInput: TextInputLayout, validationMessage: String) {
        txtInput.error = validationMessage
        txtInput.setErrorTextColor(ColorStateList.valueOf(Color.RED))
        txtInput.requestFocus()
    }


}
