package com.gpproject.adhera.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gpproject.adhera.auth.firebase.FirebaseModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repository = FirebaseRepository(
        FirebaseModule.auth,
        FirebaseModule.firestore
    )

    private val _state = MutableStateFlow(AuthFormState())

    val state: StateFlow<AuthFormState> =
        _state.asStateFlow()

    // =========================
    // TextFields Updates
    // =========================

    fun onEmailChanged(value: String) {
        _state.value = _state.value.copy(
            email = value,
            error = null
        )
    }

    fun onPasswordChanged(value: String) {
        _state.value = _state.value.copy(
            password = value,
            error = null
        )
    }

    fun onConfirmPasswordChanged(value: String) {
        _state.value = _state.value.copy(
            confirmPassword = value,
            error = null
        )
    }

    fun onNicknameChanged(value: String) {
        _state.value = _state.value.copy(
            nickname = value
        )
    }

    fun onAgeChanged(value: String) {
        _state.value = _state.value.copy(
            age = value
        )
    }

    fun onGenderSelected(gender: Gender) {
        _state.value = _state.value.copy(
            selectedGender = gender
        )
    }

    // =========================
    // Validation
    // =========================

    private fun validateLogin(): Boolean {

        return when {

            _state.value.email.isBlank() -> {

                _state.value = _state.value.copy(
                    error = "Email can't be empty"
                )

                false
            }

            _state.value.password.isBlank() -> {

                _state.value = _state.value.copy(
                    error = "Password can't be empty"
                )

                false
            }

            _state.value.password.length < 6 -> {

                _state.value = _state.value.copy(
                    error = "Password must be at least 6 characters"
                )

                false
            }

            else -> true
        }
    }

    private fun validateSignup(): Boolean {

        return when {

            _state.value.email.isBlank() -> {

                _state.value = _state.value.copy(
                    error = "Email can't be empty"
                )

                false
            }

            _state.value.password.isBlank() -> {

                _state.value = _state.value.copy(
                    error = "Password can't be empty"
                )

                false
            }

            _state.value.password.length < 6 -> {

                _state.value = _state.value.copy(
                    error = "Password too short"
                )

                false
            }

            _state.value.confirmPassword.isBlank() -> {

                _state.value = _state.value.copy(
                    error = "Please confirm password"
                )

                false
            }

            _state.value.password != _state.value.confirmPassword -> {

                _state.value = _state.value.copy(
                    error = "Passwords don't match"
                )

                false
            }

            else -> true
        }
    }

    // =========================
    // Login
    // =========================

    fun login(

        onSuccess: () -> Unit
    ) {

        if (!validateLogin()) return

        viewModelScope.launch {

            _state.value = _state.value.copy(
                isLoading = true,
                error = null
            )

            val result = repository.login(
                email = _state.value.email,
                password = _state.value.password
            )

            result.onSuccess {

                _state.value = _state.value.copy(
                    isLoading = false
                )

                onSuccess()
            }

            result.onFailure {

                _state.value = _state.value.copy(
                    isLoading = false,
                    error = it.message ?: "Something went wrong"
                )
            }
        }
    }

    // =========================
    // Signup
    // =========================

    fun signUp(

        onSuccess: () -> Unit
    ) {

        if (!validateSignup()) return

        viewModelScope.launch {

            _state.value = _state.value.copy(
                isLoading = true,
                error = null
            )

            val result = repository.signUp(
                email = _state.value.email,
                password = _state.value.password
            )

            result.onSuccess {

                _state.value = _state.value.copy(
                    isLoading = false
                )

                onSuccess()
            }

            result.onFailure {

                _state.value = _state.value.copy(
                    isLoading = false,
                    error = it.message ?: "Something went wrong"
                )
            }
        }
    }

    // =========================
    // Reset Password
    // =========================

    fun resetPassword(

        onSuccess: () -> Unit
    ) {

        if (_state.value.email.isBlank()) {

            _state.value = _state.value.copy(
                error = "Please enter your email"
            )

            return
        }

        viewModelScope.launch {

            _state.value = _state.value.copy(
                isLoading = true,
                error = null
            )

            val result = repository.resetPassword(
                _state.value.email
            )

            result.onSuccess {

                _state.value = _state.value.copy(
                    isLoading = false
                )

                onSuccess()
            }

            result.onFailure {

                _state.value = _state.value.copy(
                    isLoading = false,
                    error = it.message
                )
            }
        }
    }

    fun saveAdditionalInfo(

        role: String,

        onSuccess: () -> Unit
    ) {

        viewModelScope.launch {

            val uid = repository.getCurrentUserId()

            val profile = UserProfile(

                uid = uid,

                email = state.value.email,

                role = role,

                nickname = state.value.nickname,

                age = state.value.age.toIntOrNull(),

                gender = state.value.selectedGender
            )

            val result = repository.saveAdditionalInfo(
                profile
            )

            result.onSuccess {

                onSuccess()
            }

            result.onFailure {

                _state.value = _state.value.copy(
                    error = it.message
                )
            }
        }
    }

    fun checkEmailVerification(

        onVerified: () -> Unit
    ) {

        viewModelScope.launch {

            repository.reloadUser()

            if (repository.isEmailVerified()) {

                onVerified()

            } else {

                _state.value = _state.value.copy(
                    error = "Email is not verified yet"
                )
            }
        }
    }
}