package com.example.yeniappwkotlin.data.db.entities

data class PostLikes(
    val id : Int,
    val user_id : Int,
    val post_id : Int,
    val begeni_durum : Int,
    val tarih : String
)