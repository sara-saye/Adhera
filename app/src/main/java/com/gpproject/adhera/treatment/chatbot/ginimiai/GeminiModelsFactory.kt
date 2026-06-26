package com.gpproject.adhera.treatment.chatbot.ginimiai

import com.gpproject.adhera.BuildConfig

object GeminiModelsFactory {

    val API_KEY: String = BuildConfig.GEMINI_API_KEY

    fun createChatRequest(promptText: String): GeminiRequest {
        val systemPrompt = """
            System Instruction: You are Adhera Assistant, a supportive, empathetic, and knowledgeable AI companion.
            Explain medical concepts in simple, reassuring terms, be transparent about app permissions, and keep a warm professional boundary.

            User Message: $promptText
        """.trimIndent()

        return GeminiRequest(
            contents = listOf(ContentPart(role = "user", parts = listOf(TextPart(systemPrompt)))),
            generationConfig = GeminiConfig(temperature = 0.7f)
        )
    }

    fun createTaskManagerRequest(taskTitle: String, taskDescription: String): GeminiRequest {
        val systemPrompt = """
            System Instruction: You are a strict task planning assistant for Adhera users.
            Break the task into 4 to 8 short, concrete steps.
            Each step must start with an action verb and be easy to complete.
            Respond ONLY as plain numbered lines. Do not include JSON, headings, introductions, markdown fences, or friendly remarks.

            Task Title: $taskTitle
            Description: $taskDescription
        """.trimIndent()

        return GeminiRequest(
            contents = listOf(ContentPart(role = "user", parts = listOf(TextPart(systemPrompt)))),
            generationConfig = GeminiConfig(temperature = 0.2f)
        )
    }
}
