package com.example.yeniappwkotlin.data.network

import android.content.Context
import com.example.yeniappwkotlin.data.db.entities.Comment
import com.example.yeniappwkotlin.data.db.entities.Post
import com.example.yeniappwkotlin.data.network.responses.AuthResponse
import com.example.yeniappwkotlin.data.network.responses.CommentResponse
import com.example.yeniappwkotlin.data.network.responses.EditResponse
import com.example.yeniappwkotlin.data.network.responses.PostResponse
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.*
import java.util.concurrent.TimeUnit

interface MyApi {

    @FormUrlEncoded
    @POST("login.php")
    suspend fun userLogin(
        @Field("email") email : String,
        @Field("password") password : String
    ) : Response<AuthResponse>

    @FormUrlEncoded()
    @POST("register.php")
    suspend fun userRegister(
        @Field("name") name : String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<AuthResponse>

    @GET("getpost.php")
    suspend fun getPost() : Response<List<Post>>

    @FormUrlEncoded
    @POST("sharepost.php")
    suspend fun savePost(
        @Field("user_id") user_id : Int,
        @Field("share_post") share_post : String,
        @Field("like_count") like_count : Int,
        @Field("comment_count") comment_count : Int
    ) : Response<EditResponse>

    @GET("get_comment.php")
    suspend fun getComment(@Query("post_id") post_id : Int) : Response<List<Comment>>

    @FormUrlEncoded
    @POST("save_comment.php")
    suspend fun saveComment(
        @Field("user_id") user_id: Int,
        @Field("post_id") post_id: Int,
        @Field("comment") comment : String,
        @Field("begeniDurum") begeniDurum : Int
    ) : Response<CommentResponse>

    @FormUrlEncoded
    @POST("update_like_count.php")
    suspend fun updateLikeCount(
        @Field("like_count") like_count: Int,
        @Field("post_id") post_id: Int
    ) : Response<CommentResponse>

    companion object{
        operator fun invoke(
            networkConnectionInterceptor: NetworkConnectionInterceptor
        ) : MyApi{
            val okkHttpClient = OkHttpClient.Builder()
                .addInterceptor(networkConnectionInterceptor)
                .build()
            return Retrofit.Builder()
                .client(okkHttpClient)
                .baseUrl("https://aytekincomezz.000webhostapp.com/YeniApp/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MyApi::class.java)
        }
    }
}