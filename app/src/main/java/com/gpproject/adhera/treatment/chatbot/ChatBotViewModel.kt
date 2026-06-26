package com.gpproject.adhera.treatment.chatbot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.collections.map

class ChatBotViewModel(
    private val repository: ChatBotRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            ChatUiState()
        )

    val uiState: StateFlow<ChatUiState> =
        _uiState.asStateFlow()

    val chatSessions =
        repository.getChatSessions()

    // FIX: نحتفظ بـ Job الخاص بـ loadChat عشان نلغيه لو اتفتح شات تاني
    private var loadChatJob: Job? = null

    fun sendMessage(
        message: String
    ) {

        if (
            message.isBlank() ||
            _uiState.value.isLoading
        ) return

        val userMessage =
            ChatMessage(
                text = message,
                isUser = true
            )

        _uiState.update {

            it.copy(
                messages = it.messages + userMessage,
                isLoading = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {

            val botReply =
                repository.sendMessage(message)

            if (botReply != null) {

                val botMessage =
                    ChatMessage(
                        text = botReply,
                        isUser = false
                    )

                _uiState.update { state ->

                    state.copy(
                        messages =
                            state.messages + botMessage,
                        isLoading = false
                    )
                }

            } else {

                _uiState.update {

                    it.copy(
                        isLoading = false,
                        errorMessage =
                            "Sorry, failed to connect to server"
                    )
                }
            }
        }
    }

    fun loadChat(
        chatId: Long
    ) {
        // FIX: إلغاء الـ collector القديم قبل ما نفتح واحد جديد
        loadChatJob?.cancel()

        loadChatJob = viewModelScope.launch {

            repository
                .getMessages(chatId)
                .collect { messages ->

                    _uiState.update {

                        it.copy(
                            messages =
                                messages.map { item ->

                                    ChatMessage(
                                        text = item.text,
                                        isUser = item.isUser,
                                        timestamp = item.timestamp
                                    )
                                },
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }
        }
    }

    fun clearChat() {

        viewModelScope.launch {

            repository.clearChatHistory()

            _uiState.value =
                ChatUiState()
        }
    }

    fun startNewChat() {

        viewModelScope.launch {

            // FIX: إلغاء أي collector قديم عشان نمنع تضارب الـ state
            loadChatJob?.cancel()
            loadChatJob = null

            try {
                repository.createNewChat()
            } catch (e: Exception) {
                // لو فشل إنشاء الـ session في الـ DB، نفضل نكمل بـ state نظيف
                // والـ sendMessage هيعمل createNewChat تاني تلقائياً
            }

            _uiState.value =
                ChatUiState()
        }
    }
}