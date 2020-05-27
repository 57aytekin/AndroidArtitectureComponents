package com.example.yeniappwkotlin.data.db.entities

data class Post (
    val id : Int? = null,
    val user_id : Int? = null,
    val name : String? = null,
    val paths : String? = null,
    val share_post : String? = null,
    val like_count : Int? = null,
    val comment_count : Int? = null,
    val tarih : String? = null
)