package com.gpproject.adhera.detection.screens.focustest

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gpproject.adhera.ui.components.*
import com.gpproject.adhera.ui.theme.*

@Composable
fun FocusTestIntroScreen(
    stageIndex: Int = 3,
    onBack: () -> Unit,
    onReady: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .adheraScreenPadding()
            .padding(horizontal = 16.dp)
    ) {
        // Header باستخدام الكومبوننت الموحد
        HeaderWithBack(
            title = "Focus Test",
            onBack = onBack,
            progress = 0.75f,
            stepText = "Step $stageIndex of 3"
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Main Card
        AnimatedEntrance(delayMillis = 100) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {

                    Text(
                        text = "How the Focus Test Works",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = NavyPrimary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "You will see several images for a short time. Try to pay attention and remember them.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    FocusStepRow(
                        icon = Icons.Default.Visibility,
                        text = "First, a few images will appear one by one."
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    FocusStepRow(
                        icon = Icons.Default.Psychology,
                        text = "Then you will see a test image."
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    FocusStepRow(
                        icon = Icons.Default.CheckCircle,
                        text = "Simply tell us if you have seen that image before."
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Hint Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = NavyPrimary.copy(alpha = 0.08f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "There are no right or wrong answers. Just stay relaxed and focus.",
                            style = MaterialTheme.typography.bodySmall,
                            color = NavyPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    PrimaryButton(
                        text = "I'm Ready",
                        onClick = onReady
                    )
                }
            }
        }
    }
}

// ====================== Helper Component ======================
@Composable
private fun FocusStepRow(
    icon: ImageVector,
    text: String
) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(NavyPrimary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = NavyPrimary,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            modifier = Modifier.weight(1f)
        )
    }
}