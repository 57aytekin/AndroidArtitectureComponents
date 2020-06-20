package com.example.yeniappwkotlin.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.yeniappwkotlin.R
import com.example.yeniappwkotlin.ui.activity.auth.LoginActivity
import com.example.yeniappwkotlin.ui.activity.splashScreen.SplashScreenActivity
import com.example.yeniappwkotlin.util.PrefUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class WelcomeActivity : AppCompatActivity() {
    lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        val isFirst: Boolean = PrefUtils.with(this).getBoolean("is_first", true)

        handler = Handler()
        handler.postDelayed({
            if (isFirst) {
                Intent(this, SplashScreenActivity::class.java).let {
                    it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(it)
                    finish()
                }
            } else {
                Intent(this, LoginActivity::class.java).let {
                    it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(it)
                    finish()
                }
            }
        }, 1000)
    }
}