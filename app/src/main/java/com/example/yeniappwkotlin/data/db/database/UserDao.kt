package com.example.yeniappwkotlin.data.db.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.yeniappwkotlin.data.db.entities.CURRENT_USER_ID
import com.example.yeniappwkotlin.data.db.entities.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(user : User) : Long

    @Query("SELECT * FROM user WHERE uid = $CURRENT_USER_ID")
    fun getUser(): LiveData<User>

    @Query("DELETE FROM user")
    fun deleteUser()

    @Query("DELETE FROM post")
    fun deletePost()

    @Query("DELETE FROM likes")
    fun deleteLikes()

    @Query("DELETE FROM messagelist")
    fun deleteMessageList()
}