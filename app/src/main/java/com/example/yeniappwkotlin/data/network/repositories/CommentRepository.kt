package com.example.yeniappwkotlin.data.network.repositories

import android.util.Log
import android.widget.Toast
import com.example.yeniappwkotlin.data.db.entities.Comment
import com.example.yeniappwkotlin.data.network.MyApi
import com.example.yeniappwkotlin.data.network.SafeApiRequest
import com.example.yeniappwkotlin.data.network.responses.CommentResponse
import com.example.yeniappwkotlin.data.network.responses.EditResponse
import com.example.yeniappwkotlin.ui.activity.comment.CommentListener
import com.example.yeniappwkotlin.util.ApiException
import com.example.yeniappwkotlin.util.NoInternetException
import retrofit2.http.Field
import java.lang.Exception

class CommentRepository(
    private val api : MyApi,
    private val post_id : Int
) : SafeApiRequest() {
    suspend fun getComments() : List<Comment>{
        var deger: List<Comment>?
        try {
            deger = apiRequest { api.getComment(post_id) }
        }catch (e: ApiException){
            deger = listOf()
            Log.d("COMMENT_1",e.message!!)
        }catch (e : NoInternetException){
            deger = listOf()

            Log.d("COMMENT_2",e.message!!)
        }catch (e : Exception){
            deger = listOf()
            Log.d("COMMENT_3",e.message!!)
        }
        return deger!!
    }
    suspend fun saveComments(user_id : Int, post_id: Int, comment : String, begeniDurum : Int) : CommentResponse {
        return apiRequest { api.saveComment(user_id, post_id, comment, begeniDurum) }
    }

    suspend fun saveLikes(post_sahibi_id : Int, comment_sahibi_id: Int, comment_id: Int, post_id: Int) : CommentResponse {
        return apiRequest { api.saveLikes(post_sahibi_id, comment_sahibi_id, comment_id, post_id) }
    }
    suspend fun updateCommentLike( commentId : Int, begeniDurum : Int  ) : CommentResponse{
        return apiRequest { api.updateCommentLike(commentId, begeniDurum) }
    }
    suspend fun pushNotification(user_name: String, other_user_name: String,  commentName: String, durum: Int) : String{
        return apiRequest { api.pushNotification(user_name, other_user_name, commentName, durum) }
    }
}