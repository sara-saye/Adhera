package com.gpproject.adhera.detection.screens.assessment

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gpproject.adhera.ui.components.*
import com.gpproject.adhera.ui.theme.*

// ====================== Assessment Screen (ViewModel-driven) ======================
@Composable
fun AssessmentScreen(
    stageIndex: Int = 2,
    totalStages: Int = 3,
    onFinished: (predictionResult: String?) -> Unit,  // لما السيرفر يرجع نتيجة
    onNavigateBack: () -> Unit,                        // للرجوع من أول سؤال
    viewModel: AssessmentViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    val questions = listOf(
        "I often make careless mistakes in my activities.",
        "I have difficulty staying focused during my activities (domestic, professional).",
        "It is difficult for me to wait my turn in a queue.",
        "I have trouble maintaining my attention at work.",
        "I don't really pay attention to details.",
        "I often leave my seat unnecessarily during a meeting.",
        "I often wiggle my hands or feet on my seat.",
        "I am often subject to forgetfulness in my daily life (doing housework, shopping...).",
        "It is difficult for me to organize tasks that require several steps.",
        "My relatives blame me for not listening to them when they talk."
    )

    // لما السيرفر يرجع نتيجة → روح للشاشة الجاية
    LaunchedEffect(uiState.isFinished) {
        if (uiState.isFinished) {
            onFinished(uiState.predictionResult)
        }
    }

    // Loading overlay
    if (uiState.isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = NavyPrimary)
        }
        return
    }

    val questionProgress by animateFloatAsState(
        targetValue = (uiState.currentQuestionIndex + 1).toFloat() / viewModel.totalQuestions.toFloat(),
        animationSpec = tween(500)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .adheraScreenPadding()
    ) {
        // Header
        HeaderWithBack(
            title = "Assessment",
            onBack = {
                if (uiState.currentQuestionIndex == 0) onNavigateBack()
                else viewModel.onBack()
            },
            progress = stageIndex.toFloat() / totalStages.toFloat(),
            stepText = "Stage $stageIndex of $totalStages"
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Question Counter & Progress
            AnimatedEntrance(delayMillis = 100) {
                Text(
                    text = "Question ${uiState.currentQuestionIndex + 1} of ${viewModel.totalQuestions}",
                    style = MaterialTheme.typography.labelLarge,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedEntrance(delayMillis = 150) {
                LinearProgressIndicator(
                    progress = questionProgress,
                    modifier = Modifier
                        .fillMaxWidth(0.4f)
                        .height(4.dp)
                        .clip(CircleShape),
                    color = NavyPrimary,
                    trackColor = DividerColor
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // The Question
            AnimatedEntrance(delayMillis = 200) {
                Text(
                    text = questions[uiState.currentQuestionIndex],
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        lineHeight = 30.sp
                    ),
                    textAlign = TextAlign.Start
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Answer Options
            val answers = listOf("Never", "Weak", "Neutral", "Often", "Always")

            AnimatedEntrance(delayMillis = 300) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    answers.forEachIndexed { index, answerText ->
                        AnswerOptionItem(
                            text = answerText,
                            isSelected = uiState.selectedAnswerIndex == index,
                            onClick = { viewModel.onAnswerSelected(index) }
                        )
                    }
                }
            }

            // Error Message
            uiState.error?.let { errorMsg ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = errorMsg,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Bottom Actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = {
                if (uiState.currentQuestionIndex == 0) onNavigateBack()
                else viewModel.onBack()
            }) {
                Text("< Previous", color = TextSecondary, fontWeight = FontWeight.Bold)
            }

            PrimaryButton(
                text = if (uiState.currentQuestionIndex == viewModel.totalQuestions - 1) "Finish" else "Next >",
                onClick = { viewModel.onNext() },
                enabled = uiState.selectedAnswerIndex != -1,
                modifier = Modifier.width(140.dp)
            )
        }
    }
}

// ====================== Answer Option Item ======================
@Composable
fun AnswerOptionItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = CardBackground,
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) NavyPrimary else DividerColor
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .border(1.dp, if (isSelected) NavyPrimary else TextSecondary, CircleShape)
                    .background(if (isSelected) NavyPrimary.copy(alpha = 0.1f) else Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(NavyPrimary))
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isSelected) TextPrimary else TextSecondary,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

// ====================== Assessment Intro Screen ======================
@Composable
fun AssessmentIntroScreen(
    stageIndex: Int = 2,
    onBack: () -> Unit,
    onReady: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .adheraScreenPadding()
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Back + Progress
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
            }

            LinearProgressIndicator(
                progress = 0.50f,
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = NavyPrimary,
                trackColor = DividerColor
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Step $stageIndex of 3",
                style = MaterialTheme.typography.labelMedium,
                color = TextSecondary,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        AnimatedEntrance(delayMillis = 100) {
            Text(
                text = "Stage $stageIndex · Self-Assessment",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Main Card
        AnimatedEntrance(delayMillis = 150) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {

                    Text(
                        text = "ADHD Self-Assessment",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Answer a short set of questions honestly. There are no right or wrong answers — this helps us understand your attention patterns.",
                        fontSize = 15.sp,
                        color = TextSecondary,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Stats Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        AssessmentStat(icon = Icons.Default.Quiz, label = "10", sub = "Questions")
                        AssessmentStat(icon = Icons.Default.Timer, label = "~3 min", sub = "Est. Time")
                        AssessmentStat(icon = Icons.Default.Psychology, label = "Honest", sub = "Answers")
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    HorizontalDivider(color = DividerColor)

                    Spacer(modifier = Modifier.height(20.dp))

                    // Steps
                    AssessmentStepRow(
                        icon = Icons.Default.ListAlt,
                        text = "You'll be shown statements about everyday attention & behavior."
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    AssessmentStepRow(
                        icon = Icons.Default.Tune,
                        text = "Rate each one on a scale from Never to Very Often."
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    AssessmentStepRow(
                        icon = Icons.Default.Lock,
                        text = "Your answers are private and used only for your personal report."
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Tip Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = NavyPrimary.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "💡 Tip: Think about how you felt over the past 6 months, not just today.",
                            fontSize = 14.sp,
                            color = NavyPrimary,
                            lineHeight = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onReady,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
                    ) {
                        Text(
                            "Start Assessment",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        }
    }
}

// ====================== Helper Composables ======================

@Composable
private fun AssessmentStat(icon: ImageVector, label: String, sub: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(NavyPrimary.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(22.dp))
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = label, fontWeight = FontWeight.Bold, color = TextPrimary, fontSize = 14.sp)
        Text(text = sub, color = TextSecondary, fontSize = 12.sp)
    }
}

@Composable
private fun AssessmentStepRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(imageVector = icon, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            fontSize = 15.sp,
            color = TextSecondary,
            modifier = Modifier.weight(1f),
            lineHeight = 21.sp
        )
    }
}