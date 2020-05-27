package com.example.yeniappwkotlin.data.db.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.yeniappwkotlin.data.db.entities.User

@Database(
    entities =[User::class],
    exportSchema = false,
    version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun getUserDao() : UserDao

    companion object{
        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also{
                instance = it
            }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "MyDatabase.db"
            ).build()
    }
}