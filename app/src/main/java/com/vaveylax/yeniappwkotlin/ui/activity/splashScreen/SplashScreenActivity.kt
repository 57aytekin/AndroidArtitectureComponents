package com.vaveylax.yeniappwkotlin.ui.activity.splashScreen

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.viewpager2.widget.ViewPager2
import com.vaveylax.yeniappwkotlin.R
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreenActivity : AppCompatActivity() {

    private val introSliderAdapter = IntroSliderAdapter(
        this,
        listOf(
            IntroSlide(
                "   Vaveylax'e Hoşgeldin",
                "Burada yaratıcılığını ortaya çıkabilir ve yeni kişilerle tanışabilirsin.",
                R.drawable.slash_screen_0,
                R.drawable.splash_0
            ),
            IntroSlide(
                "Yarım Cümleni Paylaş",
                "Diğer kullanıcıların tamamlaması için yarım cümleni paylaşabilirsin.",
                R.drawable.splash_screen_1,
                R.drawable.splash_1
            ),
            IntroSlide(
                "Tamamlamaları Beğen",
                "Yarım cümlene yapılan tamamlamaları görebilir ve bunları beğenebilirsin.",
                R.drawable.splash_screen_2,
                R.drawable.splash_2
            ),
            IntroSlide(
                "Unutma",
                "Tamamlamasını beğendiğin kişiler artık seninle iletişi kurabilir ve sana mesaj atabilir.",
                R.drawable.splash_screen_3,
                R.drawable.splash_3
            )
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        slideViewPager.adapter = introSliderAdapter
        setupIndicators()
        setCurrentIndicator(0)
        slideViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })
    }

    private fun setupIndicators() {
        val indicators = arrayOfNulls<ImageView>(introSliderAdapter.itemCount)
        val layoutParam: LinearLayout.LayoutParams =
            LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        layoutParam.setMargins(8, 0, 8, 0)
        for (i in indicators.indices) {
            indicators[i] = ImageView(applicationContext)
            indicators[i].apply {
                this?.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
                this?.layoutParams = layoutParam
                indicators_container.addView(indicators[i])
            }
        }
    }

    private fun setCurrentIndicator(index: Int) {
        val childCount = indicators_container.childCount

        for (i in 0 until childCount) {
            val imageView = indicators_container[i] as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        applicationContext,
                        R.drawable.indicator_inactive
                    )
                )
            }
        }

    }
}