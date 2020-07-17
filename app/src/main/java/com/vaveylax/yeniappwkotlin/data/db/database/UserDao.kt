package com.vaveylax.yeniappwkotlin.data.db.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.vaveylax.yeniappwkotlin.data.db.entities.CURRENT_USER_ID
import com.vaveylax.yeniappwkotlin.data.db.entities.Post
import com.vaveylax.yeniappwkotlin.data.db.entities.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(user : User) : Long

    @Query("SELECT * FROM user WHERE uid = $CURRENT_USER_ID")
    fun getUser(): LiveData<User>

    @Query("UPDATE user SET user_name= :userName, first_name= :firstName, last_name = :lastName Where uid = $CURRENT_USER_ID")
    suspend fun updateUserNames(userName : String, firstName: String, lastName : String)

    @Query("UPDATE user SET user_name= :userName, first_name= :firstName, last_name = :lastName, paths= :paths Where uid = $CURRENT_USER_ID")
    suspend fun updateUserAllData(userName : String, firstName: String, lastName : String, paths : String)

    @Query("DELETE FROM user")
    fun deleteUser()

    @Query("DELETE FROM post")
    fun deletePost()

    @Query("DELETE FROM likes")
    fun deleteLikes()

    @Query("DELETE FROM messagelist")
    fun deleteMessageList()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserPost(post: Post)
}