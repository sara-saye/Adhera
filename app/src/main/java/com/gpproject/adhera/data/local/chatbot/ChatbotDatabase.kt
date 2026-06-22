package com.gpproject.adhera.data.local.chatbot


import androidx.room.Database
import androidx.room.RoomDatabase
import com.gpproject.adhera.data.local.chatbot.ChatDao
import com.gpproject.adhera.data.local.chatbot.ChatMessageEntity
import com.gpproject.adhera.data.local.chatbot.ChatSessionEntity

@Database(
    entities = [
        ChatMessageEntity::class,
        ChatSessionEntity::class
    ],
    version = 2,
    exportSchema = false
)

abstract class AppDatabase : RoomDatabase() {

    abstract fun chatDao(): ChatDao
}