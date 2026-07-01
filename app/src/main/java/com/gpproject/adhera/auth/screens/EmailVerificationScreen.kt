package com.gpproject.adhera.auth.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gpproject.adhera.ui.components.PrimaryButton
import com.gpproject.adhera.ui.components.SecondaryButton
import com.gpproject.adhera.ui.theme.*
import com.gpproject.adhera.auth.AuthViewModel
import com.gpproject.adhera.ui.components.adheraScreenPadding

@Composable
fun EmailVerificationScreen(

    email: String,

    onVerified: () -> Unit,

    viewModel: AuthViewModel = viewModel()
) {

    val state by viewModel.state.collectAsState()

    Column(

        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .adheraScreenPadding()
            .padding(horizontal = 24.dp),

        horizontalAlignment = Alignment.CenterHorizontally

    ) {

        Spacer(modifier = Modifier.height(70.dp))

        Surface(
            modifier = Modifier.size(100.dp),
            shape = RoundedCornerShape(30.dp),
            color = SuccessColor.copy(alpha = 0.12f)
        ) {

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {

                Icon(
                    imageVector = Icons.Default.MarkEmailRead,
                    contentDescription = null,
                    tint = SuccessColor,
                    modifier = Modifier.size(50.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text(
            text = "Verification Email Sent ✨",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "We've sent a verification email to:",
            color = TextSecondary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = email,
            color = NavyPrimary,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Please check your inbox or spam folder and verify your email before continuing.",
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(40.dp))

        PrimaryButton(
            text = "I've Verified My Email",
            onClick = {

                viewModel.checkEmailVerification(

                    onVerified = onVerified
                )
            }
        )

        Spacer(modifier = Modifier.height(14.dp))

        SecondaryButton(
            text = "Check Again",
            onClick = {

                viewModel.checkEmailVerification(

                    onVerified = onVerified
                )
            },
            modifier = Modifier.fillMaxWidth()
        )

        state.error?.let {

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = it,
                color = ErrorColor,
                textAlign = TextAlign.Center
            )
        }
    }
}