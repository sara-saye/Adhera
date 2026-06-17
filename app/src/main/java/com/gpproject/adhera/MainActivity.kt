package com.gpproject.adhera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.lifecycleScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
//import com.gpproject.adhera.ui.navigation.AuthNavGraph
import com.gpproject.adhera.ui.theme.AdheraTheme
import com.gpproject.adhera.viewmodels.AuthViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var credentialManager: CredentialManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        credentialManager = CredentialManager.create(this)

        setContent {

            AdheraTheme {

//                AuthNavGraph()
            }
        }
    }


}