package com.example.yeniappwkotlin.data.db.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.yeniappwkotlin.data.db.entities.Likes


@Dao
interface LikesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAllLikes(likes: List<Likes>)

    @Query("SELECT * FROM Likes ORDER BY tarih DESC")
    fun getLikes(): LiveData<List<Likes>>

}