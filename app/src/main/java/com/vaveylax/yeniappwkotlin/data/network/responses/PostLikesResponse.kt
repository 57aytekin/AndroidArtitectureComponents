package com.vaveylax.yeniappwkotlin.data.network.responses

import com.vaveylax.yeniappwkotlin.data.db.entities.PostLikes

data class PostLikesResponse(
    val post_likes : PostLikes,
    val isSuccessful : Boolean,
    val message : String
)