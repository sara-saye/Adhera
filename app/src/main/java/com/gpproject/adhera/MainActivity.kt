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
import com.gpproject.adhera.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    // بنعرف الـ ViewModel هنا عشان نبعتله الداتا
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val credentialManager = CredentialManager.create(this)

        setContent {
            // هنا بننادي على الـ App Navigation أو الـ LoginScreen
            // وبنباصي دالة الـ Google Sign-in كـ Callback
//            AppNavigation(
//                onGoogleSignInTriggered = {
//                    triggerGoogleSignIn(credentialManager)
//                }
//            )
        }
    }

    private fun triggerGoogleSignIn(credentialManager: CredentialManager) {
        // 1. إعداد طلب جوجل (هتحتاجي الـ Web Client ID من Firebase Console)
        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false) // يظهر كل الحسابات مش بس اللي دخلت قبل كده
            .setServerClientId("YOUR_WEB_CLIENT_ID_HERE.apps.googleusercontent.com")
            .setAutoSelectEnabled(true)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        // 2. تشغيل الـ Coroutine لفتح النافذة
        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(
                    context = this@MainActivity,
                    request = request
                )

                // 3. استخراج الـ ID Token
                val credential = result.credential
                if (credential is GoogleIdTokenCredential) {
                    val idToken = credential.idToken

                    // 4. إرسال الـ Token للـ ViewModel اللي هيكلم Firebase
                    authViewModel.signInWithGoogle(idToken) {
                        Log.d("Auth", "Google Sign in Successful!")
                        // هنا تقدري تنقلي اليوزر للشاشة الرئيسية
                    }
                }
            } catch (e: GetCredentialException) {
                Log.e("Auth", "Error: ${e.message}")
                authViewModel.errorMessage = "Google Sign-in failed: ${e.message}"
            }
        }
    }
}
