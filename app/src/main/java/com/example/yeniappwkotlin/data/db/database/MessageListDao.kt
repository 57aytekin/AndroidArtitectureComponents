package com.example.yeniappwkotlin.data.db.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.yeniappwkotlin.data.db.entities.MessageList

@Dao
interface MessageListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAllMessage(messageList : List<MessageList>)

    @Query("SELECT * FROM MessageList ORDER BY messageId DESC")
    fun getMessageList() : LiveData<List<MessageList>>

    @Query("UPDATE MessageList SET tarih = :tarih, message = :message WHERE messageId = :id")
    suspend fun updateLocalMessageList(tarih : String, message : String, id : Int)
}