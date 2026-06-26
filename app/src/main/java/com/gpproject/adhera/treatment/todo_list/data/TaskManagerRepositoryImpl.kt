package com.gpproject.adhera.treatment.todo_list.data

import com.gpproject.adhera.treatment.chatbot.ginimiai.GeminiApiService
import com.gpproject.adhera.treatment.chatbot.ginimiai.GeminiModelsFactory
import com.gpproject.adhera.treatment.chatbot.ginimiai.GeminiRetrofitClient

interface TaskManagerRepository {
    suspend fun splitTask(title: String, description: String): String?
}

class TaskManagerRepositoryImpl(
    private val apiService: GeminiApiService = GeminiRetrofitClient.apiService
) : com.gpproject.adhera.treatment.todo_list.data.TaskManagerRepository {

    override suspend fun splitTask(title: String, description: String): String? {
        return try {
            val request = GeminiModelsFactory.createTaskManagerRequest(title, description)
            val response = apiService.generateContent(GeminiModelsFactory.API_KEY, request)

            if (!response.isSuccessful) return null

            response.body()
                ?.candidates
                ?.firstOrNull()
                ?.content
                ?.parts
                ?.firstOrNull()
                ?.text
                ?.takeIf { it.isNotBlank() }
        } catch (_: Exception) {
            null
        }
    }
}
