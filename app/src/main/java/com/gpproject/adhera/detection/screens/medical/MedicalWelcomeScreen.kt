package com.gpproject.adhera.detection.screens.medical


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gpproject.adhera.ui.components.adheraScreenPadding
import com.gpproject.adhera.ui.theme.*


@Composable
fun ScanQuestionScreen(
    onNextClick: (Boolean) -> Unit,
    onSkipClick: () -> Unit
) {
    var hasScan by remember { mutableStateOf<Boolean?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .adheraScreenPadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = CardBackground
            ),
            elevation = CardDefaults.cardElevation(6.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Do you have MRI or EEG scans?",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Please select an option",
                    color = TextSecondary,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(30.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    ChoiceButton(
                        text = "Yes",
                        selected = hasScan == true
                    ) {
                        hasScan = true
                    }

                    ChoiceButton(
                        text = "No",
                        selected = hasScan == false
                    ) {
                        hasScan = false
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            OutlinedButton(
                onClick = onSkipClick,
                border = BorderStroke(
                    1.dp,
                    DividerColor
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = TextSecondary
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Skip")
            }

            Button(
                onClick = {
                    hasScan?.let {
                        onNextClick(it)
                    }
                },
                enabled = hasScan != null,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonPrimary
                )
            ) {
                Text(
                    text = "Next",
                    color = Color.White
                )
            }
        }
    }
}


@Composable
fun ChoiceButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected)
                ButtonPrimary
            else
                ButtonSecondary,

            contentColor = if (selected)
                Color.White
            else
                TextPrimary
        ),
        modifier = Modifier.width(120.dp)
    ) {
        Text(text)
    }
}