package com.vaveylax.yeniappwkotlin.data.network

import com.vaveylax.yeniappwkotlin.data.db.entities.Comment
import com.vaveylax.yeniappwkotlin.data.db.entities.Likes
import com.vaveylax.yeniappwkotlin.data.db.entities.MessageList
import com.vaveylax.yeniappwkotlin.data.db.entities.Post
import com.vaveylax.yeniappwkotlin.data.network.responses.AuthResponse
import com.vaveylax.yeniappwkotlin.data.network.responses.CommentResponse
import com.vaveylax.yeniappwkotlin.data.network.responses.EditResponse
import com.vaveylax.yeniappwkotlin.data.network.responses.PostLikesResponse
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


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

    @GET("get_user_posts.php")
    suspend fun getUserPosts(
        @Query("user_id") user_id: Int
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
    @POST("update_user_profile.php")
    suspend fun updateUserProfile(
        @Field("user_id") user_id: Int,
        @Field("user_name") user_name: String,
        @Field("user_first_name") user_first_name: String,
        @Field("user_last_name") user_last_name: String,
        @Field("image") image: String
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

    @GET("get_notification_budget.php")
    suspend fun getNotificationBudget(@Query("user_id") user_id: Int): List<MessageList>

    @FormUrlEncoded
    @POST("save_message_list.php")
    suspend fun saveMessageList(
        @Field("gonderen_id") gonderen_id: Int,
        @Field("alici_id") alici_id: Int,
        @Field("message") message: String,
        @Field("alici_new_message_count") alici_new_message_count: Int,
        @Field("gonderen_new_message_count") gonderen_new_message_count: Int
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
        @Field("alici_id") user_id: Int,
        @Field("current_user_id") current_user_id: Int,
        @Field("message") message: String,
        @Field("alici_new_message_count") alici_new_message_count: Int,
        @Field("gonderen_new_message_count") gonderen_new_message_count: Int,
        @Field("is_alici") is_alici: Int,
        @Field("is_gonderen") is_gonderen: Int
    ): Response<CommentResponse>

    @FormUrlEncoded
    @POST("update_is_login.php")
    suspend fun updateWhoIsTalking(
        @Field("user_id") user_id: Int,
        @Field("who_is_talking") whoIsTalking: Int
    ): Response<CommentResponse>

    @FormUrlEncoded
    @POST("update_user_login_statu.php")
    suspend fun updateUserLoginStatu(
        @Field("user_id") user_id: Int,
        @Field("is_login") is_login: Int
    ): Response<CommentResponse>

    @FormUrlEncoded
    @POST("update_token_is_last_login.php")
    suspend fun updateTokenIsLoginLastLogin(
        @Field("user_id") user_id: Int,
        @Field("token") token: String,
        @Field("is_login") isLogin: Int
    ): Response<CommentResponse>

    @FormUrlEncoded
    @POST("push_notification.php")
    suspend fun pushNotification(
        @Field("user_name") user_name: String,
        @Field("other_user_name") other_user_name: String,
        @Field("content_text") content_text: String,
        @Field("durum") durum: Int
    ): Response<String>

    companion object {
        operator fun invoke(
            networkConnectionInterceptor: NetworkConnectionInterceptor
        ): MyApi {
            //val gson = GsonBuilder().setDateFormat(DateFormat.FULL, DateFormat.FULL).create()
            val okkHttpClient = OkHttpClient.Builder()
                .addInterceptor(networkConnectionInterceptor)
                .build()
            return Retrofit.Builder()
                .client(okkHttpClient)
                .baseUrl("http://vaveylasocial.online/vaveyla/prod/")
                //.baseUrl("http://vaveylasocial.online/vaveyla/dev/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MyApi::class.java)
        }
    }
}