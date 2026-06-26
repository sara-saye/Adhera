package com.gpproject.adhera.treatment.chatbot.chatbotdb


import androidx.room.Database
import androidx.room.RoomDatabase

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