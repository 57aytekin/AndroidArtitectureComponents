package com.example.yeniappwkotlin.util

import android.annotation.SuppressLint
import android.content.Context
import java.lang.Math.abs
import java.text.ParseException
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
    val diff = date2.time - date1.time
    val second = diff /1000
    return second / 60
    //return minutes / 60
}

@SuppressLint("SimpleDateFormat")
fun calculateDate (date : String) : String{
    val asd  = StringBuilder()
    val dateFormat = SimpleDateFormat("dd.M.yyyy hh:mm:ss")
    try {
        val date1 : Date = dateFormat.parse(date)
        val bugun  = Calendar.getInstance().time

        val difference : Long = abs(date1.time - bugun.time)
        val dakika : Long = difference / (1000*60)
        val saat : Long  = difference / (1000*60*60)
        val gun : Long = difference / (1000*60*60*24)

        val ay_bilgisi : Int = gun.toInt() /30

        if(dakika.toInt() == 0){
            asd.append("Az önce")
        }else if(ay_bilgisi == 0 && gun.toInt() == 0 && saat.toInt() == 0){
            asd.append(dakika.toInt())
            asd.append(" dk önce")

        }else if(ay_bilgisi == 0 && gun.toInt() == 0){
            asd.append(saat.toInt())
            asd.append(" saat önce")

        }else if(ay_bilgisi == 0){
            asd.append(gun.toInt())
            asd.append(" gun önce")
        }else {
            asd.append(ay_bilgisi)
            asd.append(" ay önce")
        }
    }catch (e: ParseException){
        e.printStackTrace()
    }
    return asd.toString()
}