package com.example.yeniappwkotlin.ui.activity.home

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.example.yeniappwkotlin.R
import com.example.yeniappwkotlin.data.network.MyApi
import com.example.yeniappwkotlin.data.network.NetworkConnectionInterceptor
import com.example.yeniappwkotlin.util.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception


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
            bottomNavigation.checkItem(R.id.nav_tab_likes)
        }else if(pushNotification != null && pushNotification == "chat_fragment"){
            navController!!.navigate(R.id.messageFragment)
            bottomNavigation.checkItem(R.id.nav_tab_message)
        }

        bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId){
                R.id.nav_tab_home -> {
                    navController!!.navigate(R.id.homeFragment)
                }
                R.id.nav_tab_likes -> {
                    navController!!.navigate(R.id.likeFragment)
                }
                R.id.nav_tab_add -> {
                    navController!!.navigate(R.id.editFragment)
                }
                R.id.nav_tab_message -> {
                    navController!!.navigate(R.id.messageFragment)
                }
                R.id.nav_tab_profile -> {
                    navController!!.navigate(R.id.profileFragment)
                }
            }
            true
        }
    }

    override fun onBackPressed() {
        when(NavHostFragment.findNavController(nav_host_fragment).currentDestination!!.id) {
            R.id.likeFragment-> {
               navController!!.navigate(R.id.homeFragment)
                bottomNavigation.checkItem(R.id.nav_tab_home)
            }
            R.id.messageFragment -> {
                navController!!.navigate(R.id.homeFragment)
                bottomNavigation.checkItem(R.id.nav_tab_home)
            }
            R.id.profileFragment -> {
                navController!!.navigate(R.id.homeFragment)
                bottomNavigation.checkItem(R.id.nav_tab_home)
            }
            R.id.editFragment -> {
                navController!!.navigate(R.id.homeFragment)
                bottomNavigation.checkItem(R.id.nav_tab_home)
            }
            R.id.homeFragment -> {
                finish()
            }
        }
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
            try {
                api.updateUserLoginStatu(userId!!,1)
            } catch (e: ApiException) {
                Log.d("MAIN_API_ERROR: ",e.message!!)
            } catch (e: NoInternetException) {
                Log.d("MAIN_NET_ERROR: ",e.message!!)
            } catch (e : Exception){
                Log.d("MAIN_EXP: ",e.printStackTrace().toString())
            }
        }
    }
}
