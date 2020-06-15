package com.example.yeniappwkotlin.util

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

fun isFetchNeeded(context: Context, keyLastSaved: String, min_hours: Int): Boolean{
    val lastDate = PrefUtils.with(context).getLastSavedAt(keyLastSaved)
    val sonDate = SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.ENGLISH).format(Date())
    val formatter = SimpleDateFormat("MM-dd-yyyy HH:mm:ss", Locale.ENGLISH)
    val datee1 = formatter.parse(lastDate!!)
    val datee2 = formatter.parse(sonDate)
    return getHoursDiff(datee1!!, datee2!!) > min_hours
}

fun getHoursDiff(date1 : Date, date2 : Date) : Long{
    val diff = date1.time - date2.time
    val second = diff /1000
    val minutes = second / 60
    return minutes / 60
}