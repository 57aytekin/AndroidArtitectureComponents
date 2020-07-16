package com.example.yeniappwkotlin.ui.activity

import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.yeniappwkotlin.R
import com.example.yeniappwkotlin.data.db.entities.MessageList
import com.example.yeniappwkotlin.data.network.MyApi
import com.example.yeniappwkotlin.data.network.NetworkConnectionInterceptor
import com.example.yeniappwkotlin.ui.activity.auth.LoginActivity
import com.example.yeniappwkotlin.ui.activity.splashScreen.SplashScreenActivity
import com.example.yeniappwkotlin.util.*
import kotlinx.coroutines.*
import okhttp3.internal.wait
import java.lang.Exception

class WelcomeActivity : AppCompatActivity() {
    lateinit var handler: Handler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)
        val networkConnectionInterceptor = NetworkConnectionInterceptor(this)
        val api = MyApi(networkConnectionInterceptor)
        val isFirst: Boolean = PrefUtils.with(this).getBoolean("is_first", true)
        var totalBudgetCount = 0

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK

        if (isFirst) {
            Intent(this, SplashScreenActivity::class.java).let {
                it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(it)
                finish()
            }
        } else {
            val userId = PrefUtils.with(this).getInt("user_id",-1)
            if(userId != -1){
                GlobalScope.launch() {
                    runBlocking {
                        try {
                            val deger = api.getMessageList(userId)
                            if (deger.isSuccessful){
                                val messageList = deger.body()
                                for (currentMessage in messageList!!){
                                    if (userId == currentMessage.gonderen_user.user_id){
                                        if (currentMessage.gonderen_new_message_count > 0){
                                            totalBudgetCount += currentMessage.gonderen_new_message_count
                                        }
                                    }else{
                                        if (currentMessage.alici_new_message_count > 0){
                                            totalBudgetCount += currentMessage.alici_new_message_count
                                        }
                                    }
                                }
                                if (totalBudgetCount > 0){
                                    PrefUtils.with(this@WelcomeActivity).save("message_budget_count",totalBudgetCount)
                                }else {
                                    Log.d("ELSEE","ELse 1 girdi")
                                }
                            }else{
                                Log.d("HATA_2","Hataaa")
                            }
                        }catch (e : NoInternetException){
                            Coroutines.main { toast(e.message!!) }
                            //Thread.sleep(2000)
                        } catch (e : ApiException){
                            e.printStackTrace()
                        } catch (e : Exception){
                            e.printStackTrace()
                        }
                    }
                    startActivity(intent)
                    finish()
                }
            }else{
                startActivity(intent)
                finish()
            }

        }
    }
}