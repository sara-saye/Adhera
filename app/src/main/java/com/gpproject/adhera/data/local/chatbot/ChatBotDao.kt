package com.gpproject.adhera.data.local.chatbot

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.gpproject.adhera.data.local.chatbot.ChatMessageEntity
import com.gpproject.adhera.data.local.chatbot.ChatSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {

    @Insert
    suspend fun insertMessage(
        message: ChatMessageEntity
    )

    @Insert
    suspend fun insertSession(
        session: ChatSessionEntity
    ): Long

    @Query("""
        SELECT * 
        FROM chat_messages
        WHERE chatId=:chatId
        ORDER BY timestamp ASC
    """)
    fun getMessages(
        chatId: Long
    ): Flow<List<ChatMessageEntity>>

    @Query("""
        SELECT * 
        FROM chat_sessions
        ORDER BY createdAt DESC
    """)
    fun getSessions():
            Flow<List<ChatSessionEntity>>

    @Query("""
        DELETE FROM chat_messages
    """)
    suspend fun clearMessages()

    @Query("""
        DELETE FROM chat_sessions
    """)
    suspend fun clearSessions()
}