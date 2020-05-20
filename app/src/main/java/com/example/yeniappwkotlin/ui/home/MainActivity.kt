package com.example.yeniappwkotlin.ui.home

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Switch
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.yeniappwkotlin.R
import com.google.android.material.tabs.TabItem
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.luseen.spacenavigation.SpaceItem
import com.luseen.spacenavigation.SpaceOnClickListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        tabLayout.addOnTabSelectedListener(object :
            OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        navController!!.navigate(R.id.homeFragment)
                        tab.setIcon(R.drawable.ic_home_black_24dp)
                    }
                    1 -> {
                        navController!!.navigate(R.id.likeFragment)
                        tab.setIcon(R.drawable.ic_likes_black_24dp)
                    }
                    2 -> {
                        navController!!.navigate(R.id.messageFragment)
                        tab.setIcon(R.drawable.ic_message_24dp)
                    }
                    3 -> {
                        navController!!.navigate(R.id.profileFragment)
                        tab.setIcon(R.drawable.ic_profile_black_24dp)
                    }
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                Toast.makeText(applicationContext, "RESELECT", Toast.LENGTH_LONG).show()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> { tab.setIcon(R.drawable.ic_home_icon) }
                    1 -> { tab.setIcon(R.drawable.ic_likes_icon) }
                    2 -> { tab.setIcon(R.drawable.ic_message_icon) }
                    3 -> { tab.setIcon(R.drawable.ic_profile_icon) }
                }
            }
        })

        fabEdit.setOnClickListener(View.OnClickListener {
            navController!!.navigate(R.id.editFragment)
        })
    }
}