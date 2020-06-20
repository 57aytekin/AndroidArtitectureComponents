package com.example.yeniappwkotlin.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

const val CURRENT_USER_ID = 0

@Entity
data class User (
    val user_id : Int? = null,
    val name : String? = null,
    val email : String? = null,
    val paths : String? = null,
    val password : String? = null,
    val is_social_account : Int? = null,
    val register_date : String? = null
){
    @PrimaryKey(autoGenerate = false)
    var uid : Int = CURRENT_USER_ID
}