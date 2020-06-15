package com.example.yeniappwkotlin.data.db.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.yeniappwkotlin.data.db.entities.Post

@Dao
interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAllPosts(posts : List<Post>)

    @Query("SELECT * FROM Post ORDER BY post_id DESC")
    fun getPosts() : LiveData<List<Post>>
}