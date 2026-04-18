package com.gpproject.adhera.ui.screens.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gpproject.adhera.ui.components.*
import com.gpproject.adhera.ui.theme.*

@Composable
fun HomeHubScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground)
            .adheraScreenPadding()
            .padding(24.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                AnimatedEntrance(delayMillis = 100) {
                    Text(
                        text = "Welcome back,",
                        color = TextSecondary
                    )
                }

                AnimatedEntrance(delayMillis = 150) {
                    Text(
                        text = "Sarsora",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            AnimatedEntrance(delayMillis = 200) {
                Surface(
                    modifier = Modifier.size(45.dp),
                    shape = CircleShape,
                    color = NavyPrimary
                ) {}
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Streak Card
        AnimatedEntrance(delayMillis = 250) {
            Surface(
                color = NavyPrimary,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "DAILY FOCUS PROGRESS",
                        color = Color.White.copy(0.7f),
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = "0 Day Streak",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    LinearProgressIndicator(
                        progress = 0f,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                            .height(6.dp)
                            .clip(CircleShape),
                        color = Color.White,
                        trackColor = Color.White.copy(0.3f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Coming Soon Box
        AnimatedEntrance(delayMillis = 350) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                color = CardBackground,
                shape = RoundedCornerShape(28.dp),
                border = BorderStroke(1.dp, DividerColor)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            tint = NavyPrimary.copy(0.3f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "More Features Coming Soon",
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary
                        )
                        Text(
                            text = "We are working on something amazing!",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}