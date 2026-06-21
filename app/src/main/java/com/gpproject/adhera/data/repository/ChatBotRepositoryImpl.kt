package com.gpproject.adhera.data.repository

import android.util.Log
import com.gpproject.adhera.data.remote.ginimiai.*

interface ChatBotRepository {
    suspend fun sendMessage(userMessage: String): String?
    fun clearChatHistory()
}

class ChatBotRepositoryImpl(
    private val apiService: GeminiApiService =
        GeminiRetrofitClient.apiService
) : ChatBotRepository {

    private val chatHistory =
        mutableListOf<ContentPart>()

    override suspend fun sendMessage(
        userMessage: String
    ): String? {

        return try {

            val request =
                GeminiModelsFactory
                    .createChatRequest(userMessage)

            val response =
                apiService.generateContent(
                    GeminiModelsFactory.API_KEY,
                    request
                )

            if (response.isSuccessful) {

                val body = response.body()

                Log.d(
                    "GeminiDebug",
                    "BODY = $body"
                )

                val botReply =
                    body
                        ?.candidates
                        ?.firstOrNull()
                        ?.content
                        ?.parts
                        ?.firstOrNull()
                        ?.text

                if (!botReply.isNullOrBlank()) {

                    chatHistory.add(
                        ContentPart(
                            role="user",
                            parts=listOf(
                                TextPart(userMessage)
                            )
                        )
                    )

                    chatHistory.add(
                        ContentPart(
                            role="model",
                            parts=listOf(
                                TextPart(botReply)
                            )
                        )
                    )

                    botReply

                } else {

                    Log.e(
                        "GeminiDebug",
                        "Parsing failed"
                    )

                    null
                }

            } else {

                Log.e(
                    "GeminiDebug",
                    """
                    Error Code=${response.code()}
                    Error=${response.errorBody()?.string()}
                    """.trimIndent()
                )

                null
            }

        } catch (e: Exception) {

            Log.e(
                "GeminiDebug",
                e.stackTraceToString()
            )

            null
        }
    }

    override fun clearChatHistory() {
        chatHistory.clear()
    }
}