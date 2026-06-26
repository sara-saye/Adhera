package com.gpproject.adhera.detection.screens.medical


sealed class PredictionUiState {
    object Idle : PredictionUiState()
    object Loading : PredictionUiState()
    data class Success(val message: String) : PredictionUiState()
    data class Error(val errorMessage: String) : PredictionUiState()
}