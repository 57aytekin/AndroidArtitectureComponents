package com.example.yeniappwkotlin.data.db.entities

import androidx.annotation.NonNull
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class MessageList(
    @SerializedName("id")
    @PrimaryKey(autoGenerate = false)
    @NonNull
    val messageId : Int? = null,
    val message : String,
    val tarih : String,
    val alici_new_message_count : Int,
    val gonderen_new_message_count : Int,
    @Embedded(prefix = "send_")
    @NonNull
    val gonderen_user : User,
    @Embedded(prefix = "receiver_")
    @NonNull
    val alici_user : User
)