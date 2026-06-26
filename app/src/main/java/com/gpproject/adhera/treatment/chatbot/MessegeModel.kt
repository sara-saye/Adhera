package com.gpproject.adhera.treatment.chatbot

// موديل بسيط ومحدد لرسائل الـ UI
data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

// حالة الشاشة بالكامل
data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)