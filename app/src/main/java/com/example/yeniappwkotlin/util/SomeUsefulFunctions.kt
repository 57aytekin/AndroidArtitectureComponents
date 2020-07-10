package com.example.yeniappwkotlin.util

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.navigation.NavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.yeniappwkotlin.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.home_fragment.*

fun directsProfile(activity: Activity,navController: NavController, imageView: CircleImageView) {
    val asd = activity.findViewById<BottomNavigationView>(R.id.bottomNavigation)
    imageView.setOnClickListener {
        navController!!.navigate(R.id.profileFragment)
        asd.checkItem(R.id.nav_tab_profile)
    }
}