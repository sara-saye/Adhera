package com.gpproject.adhera.treatment.chatbot


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ChatBotViewModelFactory(
    private val repository: ChatBotRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(ChatBotViewModel::class.java)) {

            return ChatBotViewModel(
                repository
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}