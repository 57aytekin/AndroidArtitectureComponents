package com.example.yeniappwkotlin.data.network.responses

import com.example.yeniappwkotlin.data.db.entities.Post

data class PostResponse (
    val isSuccessful : Boolean,
    val posts : List<Post>
)