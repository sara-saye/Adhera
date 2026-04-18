package com.gpproject.adhera.ui.screens.detection

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gpproject.adhera.ui.components.*
import com.gpproject.adhera.ui.theme.*

@Composable
fun MedicalUploadScreen(
    onNext: () -> Unit,
    onSkip: () -> Unit,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    var mriFileName by remember { mutableStateOf<String?>(null) }
    var eegFileName by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .adheraScreenPadding()
    ) {
        // Header
        HeaderWithBack(
            title = "Medical Records",
            onBack = onBack,
            progress = 1f / 3f,
            stepText = "Step 1 of 3"
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Titles
            AnimatedEntrance(delayMillis = 100) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Your Health Journey",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = TextPrimary
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Adding your medical scans helps our AI provide a deeper, more accurate focus analysis.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // MRI Upload Box
            AnimatedEntrance(delayMillis = 200) {
                UploadBox(
                    label = "MRI Scan",
                    uploadedFileName = mriFileName,
                    onFileSelected = { mriFileName = it }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // EEG Upload Box
            AnimatedEntrance(delayMillis = 300) {
                UploadBox(
                    label = "EEG Scan",
                    uploadedFileName = eegFileName,
                    onFileSelected = { eegFileName = it }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Privacy Note
            AnimatedEntrance(delayMillis = 400) {
                Surface(
                    color = NavyLight.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Don’t worry if you don’t have these scans ready right now. Your privacy is our priority, and you can easily skip this stage and continue your assessment whenever you like.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontStyle = FontStyle.Normal,
                            lineHeight = 20.sp
                        ),
                        color = NavyPrimary,
                        modifier = Modifier.padding(20.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Action Buttons
        AnimatedEntrance(delayMillis = 500) {
            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PrimaryButton(
                    text = "Continue to Assessment",
                    onClick = onNext
                )

                TextButton(onClick = onSkip) {
                    Text(
                        text = "Skip for now",
                        color = TextSecondary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

// ====================== UploadBox Component ======================
@Composable
fun UploadBox(
    label: String,
    uploadedFileName: String?,
    onFileSelected: (String) -> Unit
) {
    Surface(
        onClick = { onFileSelected("$label.pdf") },
        color = CardBackground,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(
            width = 2.dp,
            color = if (uploadedFileName != null) NavyPrimary else DividerColor
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(
                        if (uploadedFileName != null) NavyPrimary else NavyLight
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.CloudUpload,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = if (uploadedFileName != null) Color.White else NavyPrimary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Text Content
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = label,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = uploadedFileName ?: "Tap to upload",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (uploadedFileName != null) NavyPrimary else TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (uploadedFileName == null) {
                Spacer(modifier = Modifier.width(8.dp))
                SecondaryButton(
                    text = "Upload",
                    onClick = { onFileSelected("$label.pdf") },
                    modifier = Modifier.width(90.dp)
                )
            }
        }
    }
}