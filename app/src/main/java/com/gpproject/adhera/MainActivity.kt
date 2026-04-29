package com.gpproject.adhera

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.gpproject.adhera.data.repository.TaskRepository
import com.gpproject.adhera.ui.navigation.AdheraNavGraph
import com.gpproject.adhera.ui.theme.AdheraTheme
import com.gpproject.adhera.viewmodels.AuthViewModel
import com.gpproject.adhera.viewmodels.TaskViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    // 1. تعريف الـ Repository
    private val taskRepository = TaskRepository()

    // 2. تعريف الـ ViewModels
    private val authViewModel: AuthViewModel by viewModels()

    // تعريف الـ TaskViewModel مع تمرير الـ Repository له (عشان الـ Save يشتغل)
    private val taskViewModel: TaskViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TaskViewModel(taskRepository) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val credentialManager = CredentialManager.create(this)

        setContent {
            AdheraTheme {
                // بنمرر الـ taskViewModel للـ NavGraph عشان يوزعه على السكرينات
                AdheraNavGraph(taskViewModel = taskViewModel)
            }
        }
    }

    // دالة الـ Google Sign-in (تسيبيها زي ما هي)
    private fun triggerGoogleSignIn(credentialManager: CredentialManager) {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId("YOUR_WEB_CLIENT_ID_HERE.apps.googleusercontent.com")
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(
                    context = this@MainActivity,
                    request = request
                )

                val credential = result.credential
                if (credential is GoogleIdTokenCredential) {
                    val idToken = credential.idToken
                    authViewModel.signInWithGoogle(idToken) {
                        Log.d("Auth", "Google Sign in Successful!")
                    }
                }
            } catch (e: GetCredentialException) {
                Log.e("Auth", "Error: ${e.message}")
            }
        }
    }
}