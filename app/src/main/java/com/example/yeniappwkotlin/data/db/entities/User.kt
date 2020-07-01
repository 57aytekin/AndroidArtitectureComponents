package com.example.yeniappwkotlin.data.db.entities

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

const val CURRENT_USER_ID = 0

@Entity
data class User (
    val user_id : Int? = null,
    val user_name : String? = null,
    val first_name : String? = null,
    val last_name : String? = null,
    val last_login : String? = null,
    val email : String? = null,
    val paths : String? = null,
    val phone : String? = null,
    val password : String? = null,
    val is_social_account : Int? = null,
    val who_is_talking : Int? = null,
    val register_date : String? = null
){
    @PrimaryKey(autoGenerate = false)
    @NonNull
    var uid : Int = CURRENT_USER_ID
}