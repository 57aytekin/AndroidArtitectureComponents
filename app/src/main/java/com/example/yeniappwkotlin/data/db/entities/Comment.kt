package com.example.yeniappwkotlin.data.db.entities

data class Comment (
    val id : Int? = null,
    val user_name : String? = null,
    val first_name : String? = null,
    val user_id : Int? = null,
    val post_id : Int? = null,
    val comment : String? = null,
    val paths : String? = null,
    val is_social_account : Int? = null,
    val begeniDurum : Int? = null,
    val tarih : String? = null
)