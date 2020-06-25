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
    val user_name : String? = null,
    val first_name : String? = null,
    val paths : String? = null,
    val share_post : String? = null,
    var like_count : Int? = null,
    val comment_count : Int? = null,
    val tarih : String? = null,
    val is_social_account : Int? = null,
    @Embedded
    @NonNull
    var user_post_likes : PostLikes? = null
)