package com.example.yeniappwkotlin.data.db.entities

data class User (
    val id :Int? = null,
    val name : String? = null,
    val email : String? = null,
    var password : String? = null
)