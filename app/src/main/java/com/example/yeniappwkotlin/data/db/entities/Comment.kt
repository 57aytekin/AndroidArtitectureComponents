package com.example.yeniappwkotlin.data.db.entities

data class Comment (
    val id : Int? = null,
    val name : String? = null,
    val user_id : Int? = null,
    val post_id : Int? = null,
    val comment : String? = null,
    val begeniDurum : Int? = null,
    val tarih : String? = null
)