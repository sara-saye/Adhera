package com.gpproject.adhera.viewmodels

data class AuthUiState(

    val isLoading: Boolean = false,

    val error: String? = null,

    val success: Boolean = false
)