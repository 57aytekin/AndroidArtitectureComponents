package com.example.yeniappwkotlin.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

const val URL : String = "http://aytekincomezz.000webhostapp.com/YeniApp/"
@BindingAdapter("paths")
fun loadImage(view : CircleImageView, url : String?){
    Glide.with(view).load(URL+url).into(view)
}