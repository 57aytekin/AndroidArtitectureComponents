package com.example.yeniappwkotlin.util

import androidx.room.TypeConverter
import com.google.gson.*
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

class TimestampConverter{
    @TypeConverter
    fun fromString(value: String?): Date? {
        return try {
            val dateFormat = SimpleDateFormat("dd.M.yyyy HH:mm:ss")
            dateFormat.parse(value!!)!!
        }catch (e :Exception){
            e.printStackTrace()
            null
        }
    }

    @TypeConverter
    fun fromDate(value: Date?): String? {
        return try {
            val dateFormat = SimpleDateFormat("dd.M.yyyy HH:mm:ss")
            dateFormat.format(value)
        }catch (e :Exception){
            e.printStackTrace()
            null
        }
    }


}