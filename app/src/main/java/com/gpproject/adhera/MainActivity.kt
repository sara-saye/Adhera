package com.gpproject.adhera

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.gpproject.adhera.ui.navigation.AdheraNavGraph
import com.gpproject.adhera.ui.theme.AdheraTheme
import com.gpproject.adhera.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    // الـ ViewModels الأساسية للأبلكيشن
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val credentialManager = CredentialManager.create(this)

        setContent {
            com.gpproject.adhera.ui.theme.AdheraTheme { // استخدمي المسار الكامل هنا للآمان
                AdheraNavGraph()
            }
        }
    }

    // دالة الـ Google Sign-in (خليها موجودة عشان لما ترجعي لخطوة الـ Auth)
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