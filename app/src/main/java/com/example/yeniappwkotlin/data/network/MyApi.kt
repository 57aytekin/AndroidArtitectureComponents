package com.example.yeniappwkotlin.data.network

import android.content.Context
import com.example.yeniappwkotlin.data.db.entities.*
import com.example.yeniappwkotlin.data.network.responses.*
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
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<AuthResponse>

    @FormUrlEncoded()
    @POST("register.php")
    suspend fun userRegister(
        @Field("user_name") userName: String,
        @Field("first_name") firstName: String,
        @Field("last_name") lastName: String,
        @Field("email") email: String,
        @Field("phone") phone: String,
        @Field("password") password: String,
        @Field("paths") paths: String,
        @Field("is_social_account") is_social_account: Int
    ): Response<AuthResponse>

    @GET("getpost.php")
    suspend fun getPost(
        @Query("user_id") user_id: Int,
        @Query("page") page: Int,
        @Query("row_per_page") row_per_page : Int
    ): Response<List<Post>>

    @FormUrlEncoded
    @POST("sharepost.php")
    suspend fun savePost(
        @Field("user_id") user_id: Int,
        @Field("share_post") share_post: String,
        @Field("like_count") like_count: Int,
        @Field("comment_count") comment_count: Int
    ): Response<EditResponse>

    @GET("get_comment.php")
    suspend fun getComment(@Query("post_id") post_id: Int): Response<List<Comment>>

    @FormUrlEncoded
    @POST("save_comment.php")
    suspend fun saveComment(
        @Field("user_id") user_id: Int,
        @Field("post_id") post_id: Int,
        @Field("comment") comment: String,
        @Field("begeniDurum") begeniDurum: Int
    ): Response<CommentResponse>

    @FormUrlEncoded
    @POST("update_post_like.php")
    suspend fun updateLikeCount(
        @Field("post_id") post_id: Int,
        @Field("user_id") user_id: Int,
        @Field("like_count") like_count: Int,
        @Field("begeni_durum") begeni_durum: Int
    ): Response<CommentResponse>

    @FormUrlEncoded
    @POST("save_user_post_likes.php")
    suspend fun saveUserPostLikes(
        @Field("user_id") user_id: Int,
        @Field("post_id") post_id: Int,
        @Field("begeni_durum") begeni_durum: Int,
        @Field("like_count") like_count: Int,
        @Field("post_sahibi_id") post_sahibi_id: Int
    ): Response<PostLikesResponse>

    @GET("get_user_post_likes.php")
    suspend fun getUserPostLikes(@Query("user_id") user_id: Int): Response<PostLikesResponse>

    @GET("get_likes.php")
    suspend fun getLikes(@Query("comment_sahibi_id") comment_sahibi_id: Int): Response<List<Likes>>

    @FormUrlEncoded
    @POST("save_likes.php")
    suspend fun saveLikes(
        @Field("post_sahibi_id") post_sahibi_id: Int,
        @Field("comment_sahibi_id") comment_sahibi_id: Int,
        @Field("comment_id") comment_id: Int,
        @Field("post_id") post_id: Int
    ): Response<CommentResponse>

    @FormUrlEncoded
    @POST("update_comment_like.php")
    suspend fun updateCommentLike(
        @Field("comment_id") comment_id: Int,
        @Field("begeniDurum") begeniDurum: Int
    ): Response<CommentResponse>

    @GET("get_messagelist.php")
    suspend fun getMessageList(@Query("user_id") user_id: Int): Response<List<MessageList>>

    @FormUrlEncoded
    @POST("save_message_list.php")
    suspend fun saveMessageList(
        @Field("gonderen_id") gonderen_id: Int,
        @Field("alici_id") alici_id: Int,
        @Field("alici_name") alici_name: String,
        @Field("alici_photo") alici_photo: String,
        @Field("message") message: String
    ): Response<CommentResponse>

    @GET("get_message.php")
    suspend fun getMessage(
        @Query("gonderen_id") gonderen_id: Int,
        @Query("alici_id") alici_id: Int
    ): Response<List<MessageList>>

    @FormUrlEncoded
    @POST("update_messagelist.php")
    suspend fun updateMessageList(
        @Field("id") id: Int,
        @Field("message") message: String
    ): Response<CommentResponse>

    companion object {
        operator fun invoke(
            networkConnectionInterceptor: NetworkConnectionInterceptor
        ): MyApi {
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