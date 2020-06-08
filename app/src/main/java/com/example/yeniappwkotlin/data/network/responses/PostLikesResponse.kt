package com.example.yeniappwkotlin.data.network.responses

import com.example.yeniappwkotlin.data.db.entities.PostLikes

data class PostLikesResponse(
    val post_likes : PostLikes,
    val isSuccessful : Boolean,
    val message : String
)