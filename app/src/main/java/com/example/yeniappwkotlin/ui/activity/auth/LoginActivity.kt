package com.example.yeniappwkotlin.ui.activity.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.example.yeniappwkotlin.data.network.responses.AuthResponse
import com.example.yeniappwkotlin.databinding.ActivityLoginBinding
import com.example.yeniappwkotlin.ui.activity.home.MainActivity
import com.example.yeniappwkotlin.util.*
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.launch
import java.util.*


class LoginActivity : AppCompatActivity() {

    companion object {
        private const val RC_SIGN_IN = 120
    }

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: AuthViewModel
    private lateinit var mAuth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient

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
                PrefUtils.with(this).save("user_id", user.user_id!!)
                PrefUtils.with(this).save("user_name", user.user_name!!)
                PrefUtils.with(this).save("user_first_name", user.first_name!!)
                PrefUtils.with(this).save("user_last_name", user.last_name!!)
                PrefUtils.with(this).save("user_image", user.paths!!)
                PrefUtils.with(this).save("is_social_account", user.is_social_account!!)
                Intent(this, MainActivity::class.java).also {
                    it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(it)
                }
            }
        })


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        //Firebase Auth instance
        mAuth = FirebaseAuth.getInstance()
        sign_button.setOnClickListener {
            signIn()
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etLoginEmail.text.toString().trim()
            val password = binding.etLoginPassword.text.toString().trim()
            progress_bar.show()
            loginUser(
                "user_name",
                "fist_name",
                "last_name",
                email,
                "phone",
                password,
                "default.jpg",
                0,
                false
            )
        }
        binding.textViewLoginRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun signIn() {
        val signInIntent: Intent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        progress_bar.show()
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val exception = task.exception
            if (task.isSuccessful) {
                try {
                    PrefUtils.with(this).save("is_first", false)
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)!!
                    Log.d("SignInActivity", "firebaseAuthWithGoogle:" + account.id)
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                    toast("Google sign in failed")
                    Log.w("SignInActivity", "Google sign in failed", e)
                    progress_bar.hide()
                }
            } else {
                Log.w("SignInActivity", exception.toString())
                toast(exception.toString())
                progress_bar.hide()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = mAuth.currentUser
                    val userEmail = user?.email?.split("@")?.get(0)
                    val userPhone = if (user?.phoneNumber != null) user.phoneNumber else "000"
                    val userFistLast = user?.displayName?.split(" ")
                    val firstName = userFistLast?.get(0)
                    val lastName = userFistLast?.get(1)
                    loginUser(
                        userEmail!!,
                        firstName!!,
                        lastName!!,
                        user.email!!,
                        userPhone!!,
                        user.uid,
                        user.photoUrl.toString(),
                        1,
                        true
                    )
                } else {
                    progress_bar.handler
                    toast("signInWithCredential:failure${task.exception}")
                    Log.w("SignInActivity", "signInWithCredential:failure", task.exception)
                }
            }
    }

    private fun loginUser(
        userName: String,
        fist_name: String,
        last_name: String,
        email: String,
        phone: String,
        password: String,
        paths: String,
        isSocialAccount: Int,
        isGoogleSignIn: Boolean
    ) {
        PrefUtils.with(this).save("is_first", false)
        lifecycleScope.launch {
            try {
                val authResponse: AuthResponse = if (isGoogleSignIn) {
                    viewModel.userRegister(
                        userName,
                        fist_name,
                        last_name,
                        email,
                        phone,
                        password,
                        paths,
                        isSocialAccount
                    )
                } else {
                    viewModel.userLogin(email, password)
                }
                if (authResponse.login != null) {
                    progress_bar.hide()
                    viewModel.saveLoggedInUser(authResponse.login)
                } else {
                    progress_bar.hide()
                    binding.rootLayout.snackbar(authResponse.message!!)
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            } catch (e: NoInternetException) {
                e.printStackTrace()
            }
        }
    }

}
