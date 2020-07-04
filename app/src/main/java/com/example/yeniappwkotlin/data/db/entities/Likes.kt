package com.example.yeniappwkotlin.data.db.entities

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.yeniappwkotlin.util.TimestampConverter
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity
data class Likes (
    @PrimaryKey(autoGenerate = false)
    @SerializedName("id")
    val likes_id : Int,
    val post_id : Int,
    val post_sahibi_id : Int,
    val comment_sahibi_id : Int,
    @NonNull
    val comment_id : Int,
    val user_name : String,
    val first_name : String,
    val last_name : String,
    val paths : String,
    @NonNull
    val comment : String? = null,
    @NonNull
    val share_post : String? = null,
    @NonNull
    val tarih : String? = null,
    val is_social_account : Int
)