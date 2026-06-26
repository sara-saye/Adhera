package com.gpproject.adhera.auth

data class AuthFormState(

    val email: String = "",

    val password: String = "",

    val confirmPassword: String = "",

    val age: String = "",

    val nickname: String = "",

    val selectedGender: Gender? = null,

    val isLoading: Boolean = false,

    val error: String? = null
)