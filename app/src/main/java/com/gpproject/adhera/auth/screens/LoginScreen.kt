package com.gpproject.adhera.auth.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gpproject.adhera.ui.components.*
import com.gpproject.adhera.ui.theme.*
import com.gpproject.adhera.auth.AuthViewModel

@Composable
fun LoginScreen(

    onLoginSuccess: (String?) -> Unit,

    onNavigateToSignup: () -> Unit,

    onForgotPassword: () -> Unit,

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
            emoji = "👋",
            title = "Welcome Back",
            subtitle = "Login to continue your journey with Adhera"
        )

        Spacer(modifier = Modifier.height(40.dp))

        AuthTextField(
            value = state.email,
            onValueChange = viewModel::onEmailChanged,
            label = "Email"
        )

        Spacer(modifier = Modifier.height(16.dp))

        AuthTextField(
            value = state.password,
            onValueChange = viewModel::onPasswordChanged,
            label = "Password",
            isPassword = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {

            Text(
                text = "Forgot Password?",
                color = NavyPrimary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable {
                    onForgotPassword()
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        PrimaryButton(
            text = "Login",
            onClick = {

                viewModel.login(
                    onSuccess = onLoginSuccess
                )
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row {

            Text(
                text = "Don't have an account?",
                color = TextSecondary
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "Sign Up",
                color = NavyPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    onNavigateToSignup()
                }
            )
        }

        state.error?.let {

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = it,
                color = ErrorColor
            )
        }
    }
}
