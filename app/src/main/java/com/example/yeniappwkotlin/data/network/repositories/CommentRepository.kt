package com.example.yeniappwkotlin.data.network.repositories

import com.example.yeniappwkotlin.data.network.MyApi
import com.example.yeniappwkotlin.data.network.SafeApiRequest
import com.example.yeniappwkotlin.data.network.responses.CommentResponse

class CommentRepository(
    private val api : MyApi,
    private val post_id : Int
) : SafeApiRequest() {
    suspend fun getComments() = apiRequest { api.getComment(post_id) }
    suspend fun saveComments(user_id : Int, post_id: Int, comment : String, begeniDurum : Int) : CommentResponse {
        return apiRequest { api.saveComment(user_id, post_id, comment, begeniDurum) }
    }
}