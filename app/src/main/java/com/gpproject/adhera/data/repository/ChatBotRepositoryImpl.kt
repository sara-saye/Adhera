package com.gpproject.adhera.data.repository

import android.util.Log
import com.gpproject.adhera.data.local.chatbot.ChatDao
import com.gpproject.adhera.data.local.chatbot.ChatMessageEntity
import com.gpproject.adhera.data.local.chatbot.ChatSessionEntity
import com.gpproject.adhera.data.remote.ginimiai.ContentPart
import com.gpproject.adhera.data.remote.ginimiai.GeminiApiService
import com.gpproject.adhera.data.remote.ginimiai.GeminiModelsFactory
import com.gpproject.adhera.data.remote.ginimiai.GeminiRetrofitClient
import com.gpproject.adhera.data.remote.ginimiai.TextPart
import kotlinx.coroutines.flow.Flow

interface ChatBotRepository {

    suspend fun sendMessage(
        userMessage: String
    ): String?

    suspend fun createNewChat()

    fun getChatSessions():
            Flow<List<ChatSessionEntity>>

    fun getMessages(
        chatId: Long
    ): Flow<List<ChatMessageEntity>>

    suspend fun clearChatHistory()
}

class ChatBotRepositoryImpl(

    private val dao: ChatDao,

    private val apiService: GeminiApiService =
        GeminiRetrofitClient.apiService

) : ChatBotRepository {

    private var currentChatId = -1L

    override suspend fun createNewChat() {

        currentChatId =
            dao.insertSession(
                ChatSessionEntity(
                    title = "New Chat",
                    createdAt = System.currentTimeMillis()
                )
            )
    }

    override suspend fun sendMessage(
        userMessage: String
    ): String? {

        try {

            if (currentChatId == -1L) {
                createNewChat()
            }

            dao.insertMessage(
                ChatMessageEntity(
                    chatId = currentChatId,
                    text = userMessage,
                    isUser = true,
                    timestamp = System.currentTimeMillis()
                )
            )

            val request =
                GeminiModelsFactory
                    .createChatRequest(
                        userMessage
                    )

            val response =
                apiService.generateContent(
                    GeminiModelsFactory.API_KEY,
                    request
                )

            // FIX: تحقق إن الـ response ناجح قبل ما تحاول تقرأ الـ body
            if (!response.isSuccessful) {
                Log.e(
                    "ChatError",
                    "API error: ${response.code()} ${response.errorBody()?.string()}"
                )
                return null
            }

            val botReply =
                response.body()
                    ?.candidates
                    ?.firstOrNull()
                    ?.content
                    ?.parts
                    ?.firstOrNull()
                    ?.text

            botReply?.let {

                dao.insertMessage(
                    ChatMessageEntity(
                        chatId = currentChatId,
                        text = it,
                        isUser = false,
                        timestamp = System.currentTimeMillis()
                    )
                )
            }

            return botReply

        } catch (e: Exception) {

            Log.e(
                "ChatError",
                e.stackTraceToString()
            )

            return null
        }
    }

    override fun getChatSessions() =
        dao.getSessions()

    override fun getMessages(
        chatId: Long
    ) =
        dao.getMessages(chatId)

    override suspend fun clearChatHistory() {

        dao.clearMessages()
        dao.clearSessions()

        // FIX: كان -1 بدون L يعني Int مش Long — type mismatch
        currentChatId = -1L
    }
}