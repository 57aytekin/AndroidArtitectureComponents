package com.example.yeniappwkotlin.data.network.repositories

import com.example.yeniappwkotlin.data.network.MyApi
import com.example.yeniappwkotlin.data.network.SafeApiRequest

class CommentRepository(
    private val api : MyApi,
    private val post_id : Int
) : SafeApiRequest() {
    suspend fun getComments() = apiRequest { api.getComment(post_id) }
}