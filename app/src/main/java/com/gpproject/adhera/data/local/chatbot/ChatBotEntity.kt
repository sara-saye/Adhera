package com.gpproject.adhera.data.local.chatbot

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    val chatId: Long,

    val text: String,

    val isUser: Boolean,

    val timestamp: Long
)