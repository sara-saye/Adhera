package com.gpproject.adhera.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gpproject.adhera.ui.theme.*

@Composable
fun UploadBox(
    label: String,
    uploadedFileName: String?,
    onUploadClick: () -> Unit
) {
    Surface(
        onClick = onUploadClick,
        color = CardBackground,
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(2.dp, if (uploadedFileName != null) NavyPrimary else DividerColor),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(if (uploadedFileName != null) NavyPrimary else NavyLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.CloudUpload,
                    null,
                    modifier = Modifier.size(24.dp),
                    tint = if (uploadedFileName != null) Color.White else NavyPrimary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = uploadedFileName ?: "Tap to upload",
                    color = if (uploadedFileName != null) NavyPrimary else TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            if (uploadedFileName == null) {
                SecondaryButton(text = "Upload", onClick = onUploadClick, modifier = Modifier.width(90.dp))
            }
        }
    }
}