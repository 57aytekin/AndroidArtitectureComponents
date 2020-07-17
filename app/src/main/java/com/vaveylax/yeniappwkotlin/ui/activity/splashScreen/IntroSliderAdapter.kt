package com.vaveylax.yeniappwkotlin.ui.activity.splashScreen

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vaveylax.yeniappwkotlin.R
import com.vaveylax.yeniappwkotlin.ui.activity.auth.LoginActivity
import com.vaveylax.yeniappwkotlin.util.PrefUtils
import kotlinx.android.synthetic.main.slide_layout.view.*
import kotlinx.android.synthetic.main.slide_layout.view.btn_login

class IntroSliderAdapter(
    private val context :Context,
    private val introSlides: List<IntroSlide> ) :
    RecyclerView.Adapter<IntroSliderAdapter.IntroSliderViewHolder>() {

    inner class IntroSliderViewHolder(val view : View) : RecyclerView.ViewHolder(view){
        fun bind(introSlide: IntroSlide){
            view.tv_title.text = introSlide.title
            view.tv_description.text = introSlide.description
            view.iv_slide.setImageResource(introSlide.icon)
            view.iv_title.setImageResource(introSlide.titleIcon)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntroSliderViewHolder {
        return IntroSliderViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.slide_layout,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return introSlides.size
    }

    override fun onBindViewHolder(holder: IntroSliderViewHolder, position: Int) {
        holder.bind(introSlides[position])
        if (introSlides.size-1 == position){
            holder.itemView.btn_login.visibility = View.VISIBLE
        }else{
            holder.itemView.btn_login.visibility = View.INVISIBLE
        }
        holder.itemView.btn_login.setOnClickListener {
            //Add shared preferences for dont show again
            PrefUtils.with(context).save("is_first",false)
            Intent(context, LoginActivity::class.java).let {
                it.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(it)
                (context as SplashScreenActivity).finish()
            }
        }
    }


}