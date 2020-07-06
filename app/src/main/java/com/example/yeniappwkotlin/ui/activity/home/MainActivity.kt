package com.example.yeniappwkotlin.ui.activity.home

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.yeniappwkotlin.R
import com.example.yeniappwkotlin.data.network.MyApi
import com.example.yeniappwkotlin.data.network.NetworkConnectionInterceptor
import com.example.yeniappwkotlin.util.Coroutines
import com.example.yeniappwkotlin.util.PrefUtils
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var navController: NavController? = null
    private lateinit var mAuth : FirebaseAuth
    private lateinit var api : MyApi
    private var userId : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val networkConnectionInterceptor = NetworkConnectionInterceptor(this)
        api = MyApi(networkConnectionInterceptor)
        userId = PrefUtils.with(this).getInt("user_id",-1)

        val pushNotification = intent.getStringExtra("push_fragment")

        navController = Navigation.findNavController(this, R.id.nav_host_fragment)

        if (pushNotification != null && pushNotification == "like_fragment"){
            navController!!.navigate(R.id.likeFragment)
        }else if(pushNotification != null && pushNotification == "chat_fragment"){
            navController!!.navigate(R.id.messageFragment)
        }
        tabLayout.addOnTabSelectedListener(object :
            OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        navController!!.navigate(R.id.homeFragment)
                        tab.setIcon(R.drawable.icon_new_home_filled)
                    }
                    1 -> {
                        navController!!.navigate(R.id.likeFragment)
                        tab.setIcon(R.drawable.icon_new_like_fill)
                    }
                    2 -> {
                        navController!!.navigate(R.id.editFragment)
                        tab.setIcon(R.drawable.icon_new_plus_fill)
                    }
                    3 -> {
                        navController!!.navigate(R.id.messageFragment)
                        tab.setIcon(R.drawable.icon_new_message_fill)
                    }
                    4 -> {
                        navController!!.navigate(R.id.profileFragment)
                        tab.setIcon(R.drawable.icon_new_profile_fill)
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        navController!!.navigate(R.id.homeFragment)
                        tab.setIcon(R.drawable.icon_new_home_filled)
                    }
                    1 -> {
                        navController!!.navigate(R.id.likeFragment)
                        tab.setIcon(R.drawable.icon_new_like_fill)
                    }
                    2 -> {
                        navController!!.navigate(R.id.editFragment)
                        tab.setIcon(R.drawable.icon_new_plus_fill)
                    }
                    3 -> {
                        navController!!.navigate(R.id.messageFragment)
                        tab.setIcon(R.drawable.icon_new_message_fill)
                    }
                    4 -> {
                        navController!!.navigate(R.id.profileFragment)
                        tab.setIcon(R.drawable.icon_new_profile_fill)
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> { tab.setIcon(R.drawable.icon_new_home) }
                    1 -> { tab.setIcon(R.drawable.icon_new_like) }
                    2 -> { tab.setIcon(R.drawable.icon_new_plus)}
                    3 -> { tab.setIcon(R.drawable.icon_new_message) }
                    4 -> { tab.setIcon(R.drawable.icon_new_profile) }
                }
            }
        })
    }

    override fun onPause() {
        super.onPause()
        Coroutines.main {
            api.updateUserLoginStatu(userId!!,0)
        }
    }

    override fun onResume() {
        super.onResume()
        Coroutines.main {
            api.updateUserLoginStatu(userId!!,1)
        }
    }
}
