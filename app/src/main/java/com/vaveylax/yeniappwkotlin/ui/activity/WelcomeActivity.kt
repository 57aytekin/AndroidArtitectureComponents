package com.vaveylax.yeniappwkotlin.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.vaveylax.yeniappwkotlin.R
import com.vaveylax.yeniappwkotlin.data.network.MyApi
import com.vaveylax.yeniappwkotlin.data.network.NetworkConnectionInterceptor
import com.vaveylax.yeniappwkotlin.ui.activity.auth.LoginActivity
import com.vaveylax.yeniappwkotlin.ui.activity.splashScreen.SplashScreenActivity
import com.vaveylax.yeniappwkotlin.util.*
import kotlinx.coroutines.*
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
                            //User table get likes_budget_count
                            val likesBudgets = api.getUserLikesBudgetCount(userId)
                            if (likesBudgets.isSuccessful){
                                val budgetCount = likesBudgets.body()!!.likes_budget_count
                                PrefUtils.with(this@WelcomeActivity).save("likes_budget_count",budgetCount)
                            }else{
                                Log.d("GET_LIKES_BUDGET","ERROR")
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