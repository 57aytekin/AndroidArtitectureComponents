package com.vaveylax.yeniappwkotlin.data.network.responses

import com.vaveylax.yeniappwkotlin.data.db.entities.Post

data class PostResponse (
    val isSuccessful : Boolean,
    val posts : List<Post>
)