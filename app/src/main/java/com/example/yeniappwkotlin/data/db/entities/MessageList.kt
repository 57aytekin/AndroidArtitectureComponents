package com.example.yeniappwkotlin.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class MessageList(
    @SerializedName("id")
    @PrimaryKey(autoGenerate = false)
    val messageId : Int,
    val gonderen_id : Int,
    val alici_id : Int,
    val alici_name : String,
    val alici_photo : String,
    val message : String,
    val tarih : String,
    val name : String,
    val paths : String
)