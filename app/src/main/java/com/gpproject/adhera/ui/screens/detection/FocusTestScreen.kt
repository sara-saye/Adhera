package com.gpproject.adhera.ui.screens.detection

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gpproject.adhera.R
import com.gpproject.adhera.ui.components.*
import com.gpproject.adhera.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SynapticFlowObservationScreen(
    stageIndex: Int = 2,
    totalStages: Int = 3,
    onBack: () -> Unit,
    onFlowComplete: () -> Unit
) {
    val images = listOf(R.drawable.photo_1, R.drawable.photo_2, R.drawable.photo_4)
    var currentImageIndex by remember { mutableStateOf(0) }
    var timeLeft by remember { mutableStateOf(5) }

    LaunchedEffect(currentImageIndex) {
        timeLeft = 5
        while (timeLeft > 0) {
            delay(1000L)
            timeLeft--
        }
        if (currentImageIndex < images.size - 1) {
            currentImageIndex++
        } else {
            onFlowComplete()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .adheraScreenPadding()
            .padding(10.dp)
    ) {
        // Header
        HeaderWithBack(
            title = "Focus Test",
            onBack = onBack,
            progress = stageIndex.toFloat() / totalStages.toFloat(),
            stepText = "Stage $stageIndex of $totalStages"
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            // Timer Circle
            AnimatedEntrance(delayMillis = 100) {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = timeLeft / 5f,
                        modifier = Modifier.size(70.dp),
                        color = NavyPrimary,
                        trackColor = NavyLight,
                        strokeWidth = 4.dp
                    )
                    Text(
                        text = "$timeLeft",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = NavyPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // Image Card
            AnimatedContent(targetState = currentImageIndex) { index ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.1f),
                    shape = RoundedCornerShape(28.dp),
                    color = CardBackground,
                    shadowElevation = 4.dp
                ) {
                    Image(
                        painter = painterResource(id = images[index]),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Progress Dots
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(3) { i ->
                    Box(
                        modifier = Modifier
                            .width(if (i == currentImageIndex) 30.dp else 10.dp)
                            .height(8.dp)
                            .clip(CircleShape)
                            .background(if (i == currentImageIndex) NavyPrimary else DividerColor)
                    )
                }
            }
        }
    }
}

@Composable
fun SynapticFlowTestScreen(
    stageIndex: Int = 2,
    totalStages: Int = 3,
    testImage: Int = R.drawable.photo_3,
    onBack: () -> Unit,
    onAnswer: (Boolean) -> Unit
) {
    var isReady by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .adheraScreenPadding()
    ) {
        // Header
        HeaderWithBack(
            title = "Focus Test",
            onBack = onBack,
            progress = stageIndex.toFloat() / totalStages.toFloat(),
            stepText = "Stage $stageIndex of $totalStages"
        )

        if (!isReady) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    AnimatedEntrance(delayMillis = 100) {
                        Text(
                            text = "Visual Recognition",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    AnimatedEntrance(delayMillis = 150) {
                        Text(
                            text = "We'll show you an image. Tell us if you've seen it in the previous step.",
                            textAlign = TextAlign.Center,
                            color = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    AnimatedEntrance(delayMillis = 200) {
                        PrimaryButton(
                            text = "I'm Ready!",
                            onClick = { isReady = true }
                        )
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                AnimatedEntrance(delayMillis = 100) {
                    Text(
                        text = "Have you seen this image before?",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                        textAlign = TextAlign.Center,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.1f),
                    shape = RoundedCornerShape(28.dp),
                    color = CardBackground,
                    border = BorderStroke(1.dp, DividerColor)
                ) {
                    Image(
                        painter = painterResource(id = testImage),
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Yes / No Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    PrimaryButton(
                        text = "Yes",
                        onClick = { onAnswer(true) },
                        modifier = Modifier.weight(1f)
                    )

                    SecondaryButton(
                        text = "No",
                        onClick = { onAnswer(false) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}