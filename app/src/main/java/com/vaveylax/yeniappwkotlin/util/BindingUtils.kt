package com.vaveylax.yeniappwkotlin.util

import android.util.Log
import com.bumptech.glide.Glide
import de.hdodenhof.circleimageview.CircleImageView

const val URL : String = "http://vaveylasocial.online/vaveyla/prod/"
//const val URL : String = "http://vaveylasocial.online/vaveyla/dev/"

fun loadImage(view : CircleImageView, url : String?, is_social_account: Int?){
    try {
        if(is_social_account == 1){
            Glide.with(view).load(url).into(view)
        }else{
            Glide.with(view).load(URL+url).into(view)
        }
    }catch (e : Exception){
        Log.d("PHOTO URL ERROR ",e.message!!)
    }
}