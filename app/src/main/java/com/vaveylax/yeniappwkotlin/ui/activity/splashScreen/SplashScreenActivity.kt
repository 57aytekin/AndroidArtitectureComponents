package com.vaveylax.yeniappwkotlin.ui.activity.splashScreen

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import com.vaveylax.yeniappwkotlin.R
import com.vaveylax.yeniappwkotlin.ui.activity.auth.LoginActivity
import com.vaveylax.yeniappwkotlin.util.PrefUtils
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreenActivity : AppCompatActivity() {
    private var viewPagerAdapter: IntroSliderAdapter? = null

    override fun onStart() {
        super.onStart()
        getStarted.setOnClickListener {
            PrefUtils.with(this).save("is_first",false)
            Intent(this, LoginActivity::class.java).let {
                it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                this.startActivity(it)
                this.finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        setContentView(R.layout.activity_splash_screen)

        viewPagerAdapter = IntroSliderAdapter(supportFragmentManager)
        slideViewPager.adapter = viewPagerAdapter
        indicator.setViewPager(slideViewPager)
    }
}