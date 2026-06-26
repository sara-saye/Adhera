package com.gpproject.adhera.treatment.chatbot.ginimiai

import com.gpproject.adhera.BuildConfig

object GeminiModelsFactory {

    val API_KEY: String = BuildConfig.GEMINI_API_KEY

    // 1. تجهيز ريكويست الشات بوت والدعم النفسي
    fun createChatRequest(promptText: String): GeminiRequest {
        val systemPrompt = """
            System Instruction: You are Adhera Assistant, a supportive, empathetic, and knowledgeable AI companion. 
            Your goals are:
            1. Explain medical concepts like MRI and EEG in simple, non-scary, and encouraging terms for users.
            2. Explain clearly why the app needs camera access (e.g., for facial engagement and behavioral tracking) with transparency and care.
            3. Act as a light, friendly therapist companion—listen to the user, validate their feelings, and offer positive coping mechanisms, but always maintain a safe, warm, and professional boundary.
            
            User Message: $promptText
        """.trimIndent()

        return GeminiRequest(
            contents = listOf(ContentPart(role = "user", parts = listOf(TextPart(systemPrompt)))),
            generationConfig = GeminiConfig(temperature = 0.7f)
        )
    }

    // 2. تجهيز ريكويست إدارة وتقسيم التاسكات
    fun createTaskManagerRequest(taskTitle: String, taskDescription: String): GeminiRequest {
        val systemPrompt = """
            System Instruction: You are a strict and highly efficient Task Management Assistant for Adhera users. 
            Your single task is to take a raw task name and its description provided by the user, and break it down into clean, actionable, and structured sub-tasks or steps. 
            Respond ONLY with a well-formatted list of sub-tasks. Do not include introductory phrases, conversational fluff, or friendly remarks.
            
            Task Title: $taskTitle
            Description: $taskDescription
        """.trimIndent()

        return GeminiRequest(
            contents = listOf(ContentPart(role = "user", parts = listOf(TextPart(systemPrompt)))),
            generationConfig = GeminiConfig(temperature = 0.2f)
        )
    }
}