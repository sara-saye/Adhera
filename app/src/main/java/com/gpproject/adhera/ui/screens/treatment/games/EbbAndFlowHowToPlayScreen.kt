package com.gpproject.adhera.ui.screens.treatment.games

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─── Screen 3: How to Play ────────────────────────────────────────────────────
@Composable
fun EbbAndFlowHowToPlayScreen(
    onBack: () -> Unit = {},
    onSettings: () -> Unit = {},
    onStartGame: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            AdheraTopBar(title = "Adhera", onBack = onBack, onSettings = onSettings)
        },
        containerColor = GameColors.Background,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
            ) {
                Button(
                    onClick = onStartGame,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GameColors.NavyDark),
                ) {
                    Text(
                        text = "START GAME",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            color = Color.White,
                        ),
                    )
                }
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
        ) {
            Text(
                text = "How to Play",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = GameColors.TextPrimary,
                ),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Follow these steps to master your cognitive focus.",
                style = MaterialTheme.typography.bodyMedium.copy(color = GameColors.TextSecondary),
            )

            Spacer(Modifier.height(24.dp))

            HowToPlayStep(
                stepNumber = 1,
                title = "Identify the Rule",
                body = buildAnnotatedString {
                    append("Look at the bottom bar indicator. If ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = GameColors.PointingGreen)) {
                        append("POINTING")
                    }
                    append(" is active, respond based on the direction the leaves are ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("facing") }
                    append(". If ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = GameColors.MovingOrange)) {
                        append("MOVING")
                    }
                    append(" is active, respond based on where the leaves are ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("going") }
                    append(".")
                },
                accentColor = GameColors.TextSecondary,
            )

            Spacer(Modifier.height(16.dp))

            HowToPlayStep(
                stepNumber = 2,
                title = "Beware of Distractors",
                body = buildAnnotatedString {
                    append("In higher levels, dimmed leaves appear at the screen borders. Do ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFFCC3333))) {
                        append("NOT")
                    }
                    append(" let them confuse you — they move randomly and are only there to break your focus.")
                },
                accentColor = GameColors.MovingOrange,
            )

            Spacer(Modifier.height(16.dp))

            HowToPlayStep(
                stepNumber = 3,
                title = "Swipe to Respond",
                body = buildAnnotatedString {
                    append("Quickly ")
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("swipe your finger") }
                    append(" on the screen in the correct logical direction (Up, Down, Left, Right) before time runs out. Speed and accuracy both count!")
                },
                accentColor = GameColors.TextSecondary,
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ─── Step Card ────────────────────────────────────────────────────────────────
@Composable
fun HowToPlayStep(
    stepNumber: Int,
    title: String,
    body: androidx.compose.ui.text.AnnotatedString,
    accentColor: Color,
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GameColors.CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top,
        ) {
            // Step number circle
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (stepNumber == 2)
                            GameColors.MovingOrange.copy(alpha = 0.15f)
                        else
                            GameColors.IconBackground,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "$stepNumber",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = if (stepNumber == 2) GameColors.MovingOrange else GameColors.TextSecondary,
                    ),
                )
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = GameColors.TextPrimary,
                    ),
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = GameColors.TextSecondary,
                        lineHeight = 20.sp,
                    ),
                )
            }
        }
    }
}

// ─── Preview ──────────────────────────────────────────────────────────────────
@Preview(showSystemUi = true, device = Devices.PIXEL_4)
@Composable
fun HowToPlayPreview() {
    MaterialTheme {
        EbbAndFlowHowToPlayScreen()
    }
}