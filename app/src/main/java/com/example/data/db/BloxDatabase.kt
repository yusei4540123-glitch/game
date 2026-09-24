package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.CatalogItem
import com.example.data.model.ChatMessage
import com.example.data.model.Experience
import com.example.data.model.UserProfile

@Database(
    entities = [
        UserProfile::class,
        Experience::class,
        CatalogItem::class,
        ChatMessage::class
    ],
    version = 1,
    exportSchema = false
)
abstract class BloxDatabase : RoomDatabase() {
    abstract fun bloxDao(): BloxDao

    companion object {
        @Volatile
        private var INSTANCE: BloxDatabase? = null

        fun getDatabase(context: Context): BloxDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BloxDatabase::class.java,
                    "blox_world_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
