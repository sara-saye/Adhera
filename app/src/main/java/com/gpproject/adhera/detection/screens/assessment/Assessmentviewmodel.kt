package com.gpproject.adhera.detection.screens.assessment

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.gpproject.adhera.detection.datastore.AdheraDataStore
import com.gpproject.adhera.detection.AdheraRepository
import com.gpproject.adhera.detection.AdheraRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ====================== State ======================
data class AssessmentUiState(
    val currentQuestionIndex: Int = 0,
    val selectedAnswerIndex: Int = -1,
    val answers: List<Int> = emptyList(),
    val isLoading: Boolean = false,
    val predictionResult: String? = null,
    val error: String? = null,
    val isFinished: Boolean = false
)

// ====================== ViewModel ======================
class AssessmentViewModel(
    private val repository: AdheraRepository
) : ViewModel() {

    val totalQuestions = 10

    private val _uiState = MutableStateFlow(AssessmentUiState())
    val uiState: StateFlow<AssessmentUiState> = _uiState.asStateFlow()

    fun onAnswerSelected(answerIndex: Int) {
        _uiState.update { it.copy(selectedAnswerIndex = answerIndex) }
    }

    fun onNext() {
        Log.d("TEST", "onNext called - currentIndex: ${_uiState.value.currentQuestionIndex}, selected: ${_uiState.value.selectedAnswerIndex}")
        val state = _uiState.value
        if (state.selectedAnswerIndex == -1) return

        val updatedAnswers = state.answers + state.selectedAnswerIndex

        if (state.currentQuestionIndex == totalQuestions - 1) {
            _uiState.update { it.copy(answers = updatedAnswers, selectedAnswerIndex = -1) }
            submitToServer(updatedAnswers)
        } else {
            _uiState.update {
                it.copy(
                    currentQuestionIndex = it.currentQuestionIndex + 1,
                    selectedAnswerIndex  = -1,
                    answers              = updatedAnswers
                )
            }
        }
    }

    fun onBack() {
        val state = _uiState.value
        if (state.currentQuestionIndex == 0) return
        _uiState.update {
            it.copy(
                currentQuestionIndex = it.currentQuestionIndex - 1,
                selectedAnswerIndex  = it.answers.lastOrNull() ?: -1,
                answers              = it.answers.dropLast(1)
            )
        }
    }

    private fun submitToServer(answers: List<Int>) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val features = answers.map { it.toDouble() }
                val response = repository.predictQuestionnaire(features)

                Log.d("TEST", "isSuccessful: ${response.isSuccessful}")
                Log.d("TEST", "code: ${response.code()}")
                Log.d("TEST", "body: ${response.body()}")
                Log.d("TEST", "errorBody: ${response.errorBody()?.string()}")

                if (response.isSuccessful) {
                    _uiState.update {
                        it.copy(
                            isLoading        = false,
                            isFinished       = true,
                            predictionResult = response.body()?.prediction?.toString()
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, error = "Server error: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, error = e.message ?: "Unknown error")
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    // ====================== Factory ======================
    companion object {
        fun factory(context: Context): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val dataStore  = AdheraDataStore(context.applicationContext)
                    val repository = AdheraRepositoryImpl(dataStore = dataStore)
                    return AssessmentViewModel(repository) as T
                }
            }
    }
}