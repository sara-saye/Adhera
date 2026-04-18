package com.gpproject.adhera.ui.screens.auth


import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gpproject.adhera.R
import com.gpproject.adhera.ui.components.*
import com.gpproject.adhera.ui.theme.*

@Composable
fun SignUpScreen(onSignUpComplete: () -> Unit) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .adheraScreenPadding()           // ← المودفاير الموحد
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // العنوان الرئيسي + الوصف
        AnimatedEntrance(delayMillis = 100) {
            Text(
                text = "Welcome to Adhera 👋",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )
        }

        AnimatedEntrance(delayMillis = 200) {
            Text(
                text = "Let's set up your profile to get started",
                color = TextSecondary,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Name Field
        AnimatedEntrance(delayMillis = 300) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nickname") },
                leadingIcon = { Icon(Icons.Default.Person, null, tint = NavyPrimary) },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NavyPrimary,
                    unfocusedBorderColor = DividerColor,
                    focusedLabelColor = NavyPrimary
                )
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Email Field
        AnimatedEntrance(delayMillis = 400) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email Address") },
                leadingIcon = { Icon(Icons.Default.Email, null, tint = NavyPrimary) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NavyPrimary,
                    unfocusedBorderColor = DividerColor,
                    focusedLabelColor = NavyPrimary
                )
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Password Field
        AnimatedEntrance(delayMillis = 500) {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, null, tint = NavyPrimary) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = null,
                            tint = TextSecondary
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = NavyPrimary,
                    unfocusedBorderColor = DividerColor,
                    focusedLabelColor = NavyPrimary
                )
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Gender Label
        AnimatedEntrance(delayMillis = 550) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "I identify as",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Gender Buttons
        AnimatedEntrance(delayMillis = 600) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GenderButton(
                    label = "Male",
                    emoji = "👨",
                    selected = selectedGender == "Male",
                    onClick = { selectedGender = "Male" },
                    modifier = Modifier.weight(1f)
                )
                GenderButton(
                    label = "Female",
                    emoji = "👩",
                    selected = selectedGender == "Female",
                    onClick = { selectedGender = "Female" },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Create Account Button
        AnimatedEntrance(delayMillis = 700) {
            PrimaryButton(
                text = "Create My Account",
                onClick = {
                    when {
                        name.isBlank() -> Toast.makeText(context, "Please enter your name 👤", Toast.LENGTH_SHORT).show()
                        email.isBlank() -> Toast.makeText(context, "Please enter your email 📧", Toast.LENGTH_SHORT).show()
                        password.length < 6 -> Toast.makeText(context, "Password must be at least 6 characters 🔒", Toast.LENGTH_SHORT).show()
                        selectedGender == null -> Toast.makeText(context, "Please select your gender 🙋", Toast.LENGTH_SHORT).show()
                        else -> onSignUpComplete()
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Or Divider
        AnimatedEntrance(delayMillis = 750) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = DividerColor)
                Text(
                    text = "  or continue with  ",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = DividerColor)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Google Button
        AnimatedEntrance(delayMillis = 800) {
            SocialButton(
                text = "Continue with Google",
                icon = R.drawable.google_logo,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ====================== GenderButton ======================
@Composable
fun GenderButton(
    label: String,
    emoji: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(64.dp),
        shape = RoundedCornerShape(16.dp),
        color = if (selected) NavyPrimary else CardBackground,
        border = BorderStroke(
            width = if (selected) 0.dp else 1.5.dp,
            color = if (selected) NavyPrimary else DividerColor
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(emoji, fontSize = 22.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                fontWeight = FontWeight.SemiBold,
                color = if (selected) Color.White else TextPrimary,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

// ====================== SocialButton ======================
@Composable
fun SocialButton(
    text: String,
    icon: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, DividerColor),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}