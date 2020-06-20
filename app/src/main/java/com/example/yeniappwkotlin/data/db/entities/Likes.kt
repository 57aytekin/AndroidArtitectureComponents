package com.example.yeniappwkotlin.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Likes (
    @PrimaryKey(autoGenerate = false)
    @SerializedName("id")
    val likes_id : Int,
    val post_sahibi_id : Int,
    val comment_sahibi_id : Int,
    val comment_id : Int,
    val name : String,
    val paths : String,
    val is_social_account : Int
)