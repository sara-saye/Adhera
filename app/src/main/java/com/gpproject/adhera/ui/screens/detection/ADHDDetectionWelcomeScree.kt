package com.gpproject.adhera.ui.screens.detection

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gpproject.adhera.ui.components.*
import com.gpproject.adhera.ui.theme.*

@Composable
fun ADHDDetectionWelcomeScreen(
    onStartDetection: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .adheraScreenPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // الجزء القابل للسكرول
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Titles
            AnimatedEntrance(delayMillis = 100) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "ADHD Detection",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary,
                            fontSize = 28.sp
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Follow our professional 4-step process\nfor a comprehensive clinical assessment.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Timeline
            DetectionTimelineAnimated()

            Spacer(modifier = Modifier.height(40.dp))

            // Note
            AnimatedEntrance(delayMillis = 500) {
                Surface(
                    color = NavyLight.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "“Take your time. You can pause and return whenever you feel ready. We're with you.”",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontStyle = FontStyle.Italic,
                            lineHeight = 22.sp
                        ),
                        color = NavyPrimary,
                        modifier = Modifier.padding(20.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Start Button
        AnimatedEntrance(delayMillis = 600) {
            PrimaryButton(
                text = "Start Detection",
                onClick = onStartDetection,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )
        }
    }
}

// ====================== Detection Timeline ======================
@Composable
fun DetectionTimelineAnimated() {
    val steps = listOf(
        Triple(Icons.Default.MedicalServices, "Medical Data Analysis", "Upload your EEG or MRI scans for baseline analysis."),
        Triple(Icons.Default.Schedule, "Personality Assessment", "A guided questionnaire about your daily focus and habits."),
        Triple(Icons.Default.Lightbulb, "Interactive Focus Game", "Engage with our task model to measure attention span."),
        Triple(Icons.Default.Check, "Detailed Clinical Report", "Receive a professional summary from our specialists.")
    )

    Column(modifier = Modifier.padding(horizontal = 4.dp)) {
        steps.forEachIndexed { index, step ->
            AnimatedEntrance(delayMillis = 200 + (index * 150)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = if (index < steps.size - 1) 0.dp else 0.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(NavyLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = step.first,
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = NavyPrimary
                            )
                        }

                        if (index < steps.size - 1) {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(50.dp)
                                    .background(DividerColor)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    Column(modifier = Modifier.padding(top = 4.dp)) {
                        Text(
                            text = step.second,
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = step.third,
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            lineHeight = 20.sp
                        )
                    }
                }
            }
        }
    }
}