package com.gpproject.adhera.treatment.chatbot.chatbotdb


import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    @Volatile
    private var INSTANCE: ChatbotDatabase? = null

    fun getDatabase(
        context: Context
    ): ChatbotDatabase {

        return INSTANCE ?: synchronized(this) {

            val instance =
                Room.databaseBuilder(
                    context.applicationContext,
                    ChatbotDatabase::class.java,
                    "chatbot_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

            INSTANCE = instance

            instance
        }
    }
}