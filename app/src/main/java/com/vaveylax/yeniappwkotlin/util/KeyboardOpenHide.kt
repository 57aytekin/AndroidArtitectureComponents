package com.vaveylax.yeniappwkotlin.util

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

@SuppressLint("ServiceCast")
fun openCloseSoftKeyboard(context: Context, view: View, open : Boolean) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    if (open){
        view.requestFocus()
        // open the soft keyboard
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }else{
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}