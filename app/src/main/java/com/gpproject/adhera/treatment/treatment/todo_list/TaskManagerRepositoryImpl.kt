package com.gpproject.adhera.treatment.treatment.todo_list

import android.util.Log
import com.gpproject.adhera.treatment.chatbot.ginimiai.GeminiApiService
import com.gpproject.adhera.treatment.chatbot.ginimiai.GeminiModelsFactory
import com.gpproject.adhera.treatment.chatbot.ginimiai.GeminiRetrofitClient

interface TaskManagerRepository {
    suspend fun splitTask(title: String, description: String): String?
}

class TaskManagerRepositoryImpl(
    private val apiService: GeminiApiService = GeminiRetrofitClient.apiService
) : TaskManagerRepository {

    override suspend fun splitTask(title: String, description: String): String? {
        return try {
            val request = GeminiModelsFactory.createTaskManagerRequest(title, description)
            val response = apiService.generateContent(GeminiModelsFactory.API_KEY, request)

            if (response.isSuccessful && response.body() != null) {
                val result = response.body()?.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (result != null) {
                    result
                } else {
                    Log.w("GeminiRepoTest", "⚠️ الـ Parsing في التاسكات فشل!")
                    "فشل الـ Parsing في التاسكات"
                }
            } else {
                Log.e("GeminiRepoTest", "❌ سيرفر التاسكات رجع أيرور: ${response.code()}")
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}