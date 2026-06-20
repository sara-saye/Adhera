package com.gpproject.adhera.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gpproject.adhera.ui.components.*
import com.gpproject.adhera.ui.theme.*
import com.gpproject.adhera.viewmodels.AuthViewModel

@Composable
fun ForgotPasswordScreen(

    onBackToLogin: () -> Unit,

    viewModel: AuthViewModel = viewModel()
) {

    val state by viewModel.state.collectAsState()

    Column(

        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),

        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        Spacer(modifier = Modifier.height(48.dp))

        AuthTopSection(
            emoji = "🔐",
            title = "Forgot Password?",
            subtitle = "Don't worry, we'll help you reset it"
        )

        Spacer(modifier = Modifier.height(40.dp))

        AuthTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChanged,
            label = "Email"
        )

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            text = "Send Reset Link",
            onClick = {

                viewModel.resetPassword(

                    onSuccess = {
                        onBackToLogin()
                    }
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        SecondaryButton(
            text = "Back to Login",
            onClick = onBackToLogin,
            modifier = Modifier.fillMaxWidth()
        )
    }
}