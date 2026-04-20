package com.gpproject.adhera.viewmodels

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.gpproject.adhera.data.model.UserProfile
import com.gpproject.adhera.data.repository.AuthRepository

class AuthViewModel : ViewModel() {

    private val repo = AuthRepository()

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var name by mutableStateOf("")
    var nickname by mutableStateOf("")
    var age by mutableStateOf("")
    var selectedGender by mutableStateOf<String?>(null)

    // ضفنا المتغيرات الناقصة لربط حسابات الـ Parent والـ Child
    var isChildUsingThisPhone by mutableStateOf(true)
    var shouldLinkPhones by mutableStateOf(false)

    var loginEmail by mutableStateOf("")
    var loginPassword by mutableStateOf("")

    var errorMessage by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)

    // ================= SIGN UP =================
    fun performSignUp(role: String, onSuccess: () -> Unit) {
        val user = UserProfile(
            role = role,
            email = email,
            name = name,
            nickname = nickname,
            gender = selectedGender,
            age = age,
            childWillUseThisPhone = isChildUsingThisPhone, // تم الربط
            linkParentPhone = shouldLinkPhones // تم الربط
        )

        isLoading = true

        repo.signUp(
            email,
            password,
            user,
            onSuccess = {
                isLoading = false
                onSuccess()
            },
            onError = {
                isLoading = false
                errorMessage = it
            }
        )
    }

    // ================= LOGIN =================
    fun performLogin(onSuccess: () -> Unit) {
        isLoading = true

        repo.login(
            loginEmail,
            loginPassword,
            onSuccess = {
                isLoading = false
                onSuccess()
            },
            onError = {
                isLoading = false
                errorMessage = it
            }
        )
    }

    // ================= RESET PASSWORD =================
    // خليناها تستقبل الإيميل مباشرة عشان الـ Dialog
    fun resetPassword(targetEmail: String, onSuccess: () -> Unit) {
        repo.resetPassword(
            targetEmail,
            onSuccess = onSuccess,
            onError = { errorMessage = it }
        )
    }

    // ================= GOOGLE SIGN IN =================
    fun signInWithGoogle(idToken: String, onSuccess: () -> Unit) {
        repo.firebaseAuthWithGoogle(
            idToken,
            onSuccess = onSuccess,
            onError = { errorMessage = it }
        )
    }

    fun logout() = repo.logout()
}