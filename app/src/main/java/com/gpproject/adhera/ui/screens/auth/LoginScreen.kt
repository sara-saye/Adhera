package com.gpproject.adhera.ui.screens.auth

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gpproject.adhera.R
import com.gpproject.adhera.ui.components.*
import com.gpproject.adhera.ui.theme.*
import com.gpproject.adhera.viewmodels.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = AuthViewModel(),
    onLoginSuccess: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onGoogleSignInClick: () -> Unit // ضفنا الكول باك ده عشان جوجل
) {
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .adheraScreenPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- 1. Header Section ---
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Welcome Back! 👋",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary
        )

        Text(
            text = "We missed you! Login to continue your progress.",
            color = TextSecondary,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(48.dp))

        // --- 2. Input Fields ---
        AdheraTextField(
            value = viewModel.loginEmail,
            onValueChange = { viewModel.loginEmail = it },
            label = "Email Address",
            icon = Icons.Default.Email,
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(14.dp))

        AdheraTextField(
            value = viewModel.loginPassword,
            onValueChange = { viewModel.loginPassword = it },
            label = "Password",
            icon = Icons.Default.Lock,
            isPassword = true
        )

        // Forgot Password Link
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(
                onClick = { showForgotPasswordDialog = true }, // تعديل لفتح الـ Dialog
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Forgot Password?",
                    color = NavyPrimary,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- 3. Action Buttons ---
        PrimaryButton(
            text = "Login",
            onClick = { viewModel.performLogin(onLoginSuccess) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Navigation to Sign Up
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Don't have an account?",
                color = TextSecondary,
                style = MaterialTheme.typography.bodyMedium
            )

            TextButton(onClick = onNavigateToSignUp) {
                Text(
                    text = "Sign Up",
                    color = NavyPrimary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- 4. Social Login Section ---
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = DividerColor)
            Text(
                text = " Or continue with ",
                style = MaterialTheme.typography.labelSmall,
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            HorizontalDivider(modifier = Modifier.weight(1f), color = DividerColor)
        }

        Spacer(modifier = Modifier.height(16.dp))

        SocialButton(
            text = "Login with Google",
            icon = R.drawable.google_logo,
            modifier = Modifier.fillMaxWidth(),
            onClick = onGoogleSignInClick // تم الربط هنا
        )

        Spacer(modifier = Modifier.height(24.dp))
    }

    // --- Forgot Password Dialog ---
    if (showForgotPasswordDialog) {
        var resetEmail by remember { mutableStateOf(viewModel.loginEmail) }

        AlertDialog(
            onDismissRequest = { showForgotPasswordDialog = false },
            title = { Text("Reset Password", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Enter your email address to receive a password reset link.")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = resetEmail,
                        onValueChange = { resetEmail = it },
                        label = { Text("Email") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetPassword(resetEmail) {
                        showForgotPasswordDialog = false
                    }
                }) { Text("Send Link", color = NavyPrimary) }
            },
            dismissButton = {
                TextButton(onClick = { showForgotPasswordDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

// ui/components/Buttons.kt
@Composable
fun SocialButton(
    text: String,
    @DrawableRes icon: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, DividerColor),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = "Google Logo",
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}