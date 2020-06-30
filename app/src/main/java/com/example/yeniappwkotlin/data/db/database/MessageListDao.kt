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

    @Query(
        "UPDATE MessageList SET tarih = :tarih, message = :message, alici_new_message_count = :alici_new, gonderen_new_message_count = :gonderen_new WHERE messageId = :id")
    suspend fun updateLocalMessageList(tarih : String, message : String, id : Int, alici_new : Int, gonderen_new: Int)

    @Query("UPDATE MessageList SET alici_new_message_count = :aliciNew, gonderen_new_message_count = :gonderenNew WHERE messageId = :id")
    suspend fun updateLocalBadges(id : Int, aliciNew : Int, gonderenNew: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveLocalMessageList(messageList : MessageList)
}