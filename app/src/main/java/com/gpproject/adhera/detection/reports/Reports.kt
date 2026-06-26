package com.gpproject.adhera.detection.reports


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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import com.gpproject.adhera.R
import com.gpproject.adhera.detection.datastore.AdheraDataStore
import com.gpproject.adhera.ui.components.*
import com.gpproject.adhera.ui.theme.*

// ====================== Screen ======================

@Composable
fun DetectionResultsScreen(
    onDone: () -> Unit
) {
    val context = LocalContext.current
    val dataStore = remember { AdheraDataStore(context) }

    val viewModel: DetectionResultsViewModel = viewModel(
        factory = DetectionResultsViewModelFactory(dataStore)
    )

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppBackground),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = NavyPrimary)
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .adheraScreenPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Header
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedEntrance(delayMillis = 100) {
                Surface(
                    color = NavyLight,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "ANALYSIS COMPLETED",
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = NavyPrimary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            AnimatedEntrance(delayMillis = 150) {
                Text(
                    text = "Clinical Detection\nResults",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 32.sp
                    ),
                    textAlign = TextAlign.Center,
                    color = TextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Result Cards — only the models that were actually run
        if (uiState.modelResults.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                uiState.modelResults.forEach { result ->
                    AnimatedEntrance(delayMillis = 250) {
                        ResultItemCard(
                            title      = result.title,
                            percentage = "${result.percentage}%",
                            icon       = result.iconType.toIcon()
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Final Assessment Card
        AnimatedEntrance(delayMillis = 400) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = NavyPrimary,
                shape = RoundedCornerShape(28.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Final Assessment",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "The overall calculated probability represents the combined weight of all detection modules.",
                        color = Color.White.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "ADHD PROBABILITY",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )

                            Text(
                                text = "${uiState.finalProbability}%",
                                style = MaterialTheme.typography.displayLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = NavyPrimary
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            LinearProgressIndicator(
                                progress = uiState.finalProbability / 100f,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(CircleShape),
                                color = NavyPrimary,
                                trackColor = NavyLight
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Done Button
        AnimatedEntrance(delayMillis = 500) {
            PrimaryButton(
                text = "Done & Save Results",
                onClick = onDone
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ====================== Icon Mapper ======================

private fun ModelIconType.toIcon(): ImageVector = when (this) {
    ModelIconType.ENGAGEMENT    -> Icons.Default.Bolt
    ModelIconType.EEG           -> Icons.Default.Psychology
    ModelIconType.MRI           -> Icons.Default.Visibility
    ModelIconType.EYE_TRACKING  -> Icons.Default.Timer
    ModelIconType.QUESTIONNAIRE -> Icons.Default.Assignment
}

// ====================== Result Item Card ======================

@Composable
fun ResultItemCard(
    title: String,
    percentage: String,
    icon: ImageVector
) {
    Surface(
        color = CardBackground,
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, DividerColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(NavyLight.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = NavyPrimary,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Reliability Score",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary.copy(alpha = 0.7f)
                )
            }

            Text(
                text = percentage,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = NavyPrimary
            )
        }
    }
}

// ====================== Detection Complete Screen ======================

@Composable
fun DetectionCompleteScreen(
    onViewReport: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .adheraScreenPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        AnimatedEntrance(delayMillis = 100) {
            Text(
                text = "Analysis Complete",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        LinearProgressIndicator(
            progress = 1f,
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = NavyPrimary
        )

        Spacer(modifier = Modifier.height(80.dp))

        AnimatedEntrance(delayMillis = 200) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val composition by rememberLottieComposition(
                        LottieCompositionSpec.RawRes(R.raw.brain)
                    )

                    LottieAnimation(
                        composition = composition,
                        iterations = LottieConstants.IterateForever,
                        modifier = Modifier.size(220.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Your Results Are Ready",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "We analyzed your responses and focus patterns. Your detailed report is ready to view.",
                        fontSize = 16.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    PrimaryButton(
                        text = "View My Report",
                        onClick = onViewReport
                    )
                }
            }
        }
    }
}