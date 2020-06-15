package com.example.yeniappwkotlin.data.db.entities

import androidx.annotation.NonNull
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Post (
    @PrimaryKey(autoGenerate = false)
    @SerializedName("id")
    val post_id : Int? = null,
    val user_id : Int? = null,
    val name : String? = null,
    val paths : String? = null,
    val share_post : String? = null,
    val like_count : Int? = null,
    val comment_count : Int? = null,
    val tarih : String? = null,
    @Embedded
    @NonNull
    val user_post_likes : PostLikes? = null
)