package com.example.yeniappwkotlin.data.db.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.yeniappwkotlin.data.db.entities.Post
import com.example.yeniappwkotlin.data.db.entities.PostLikes

@Dao
interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAllPosts(posts : List<Post>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePosts(posts : List<Post>)

    @Query("SELECT * FROM Post ORDER BY post_id DESC")
    fun getPosts() : LiveData<List<Post>>

    @Query("SELECT * FROM Post ORDER BY post_id DESC")
    suspend fun getLocalPost() : List<Post>

    @Query("UPDATE Post SET like_count = :likeCount, begeni_durum = :begeniDurum WHERE post_id = :id ")
    suspend fun updateLocalPostCount(likeCount : Int, begeniDurum: Int, id : Int)
}