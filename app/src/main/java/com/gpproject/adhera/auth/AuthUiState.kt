package com.gpproject.adhera.auth

data class AuthUiState(

    val isLoading: Boolean = false,

    val error: String? = null,

    val success: Boolean = false
)