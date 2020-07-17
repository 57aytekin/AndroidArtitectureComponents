package com.vaveylax.yeniappwkotlin.util

import android.app.Activity
import androidx.navigation.NavController
import com.vaveylax.yeniappwkotlin.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import de.hdodenhof.circleimageview.CircleImageView

fun directsProfile(activity: Activity,navController: NavController, imageView: CircleImageView) {
    val asd = activity.findViewById<BottomNavigationView>(R.id.bottomNavigation)
    imageView.setOnClickListener {
        navController!!.navigate(R.id.profileFragment)
        asd.checkItem(R.id.nav_tab_profile)
    }
}