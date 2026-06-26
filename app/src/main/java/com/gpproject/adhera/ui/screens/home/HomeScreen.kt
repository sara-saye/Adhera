package com.gpproject.adhera.ui.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.PlaylistAddCheck
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gpproject.adhera.ui.components.adheraScreenPadding
import com.gpproject.adhera.ui.theme.AppBackground
import com.gpproject.adhera.ui.theme.NavyPrimary
import com.gpproject.adhera.ui.theme.TextSecondary

@Composable
fun HomeHubScreen(
    onNavigateToTodo: () -> Unit,
    onNavigateToFocusGames: () -> Unit,
    onNavigateToHabits: () -> Unit
) {
    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .adheraScreenPadding(),
        containerColor = AppBackground,
        topBar = { HomeTopBar() }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Good Morning",
                style = MaterialTheme.typography.headlineLarge,
                color = NavyPrimary,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = "Ready for a calm, structured day?",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                item {
                    HomeFeatureCard(
                        title = "Focus Games",
                        description = "Sharpen your cognitive agility.",
                        icon = Icons.Default.Gamepad,
                        actionText = "Start",
                        onClick = onNavigateToFocusGames
                    )
                }
                item {
                    HomeFeatureCard(
                        title = "To-Do",
                        description = "Manage your priority tasks.",
                        icon = Icons.AutoMirrored.Filled.PlaylistAddCheck,
                        actionText = "View",
                        onClick = onNavigateToTodo
                    )
                }
                item {
                    HomeFeatureCard(
                        title = "Brain Dump",
                        description = "Offload lingering thoughts quickly.",
                        icon = Icons.Default.Lightbulb,
                        actionText = "Capture",
                        onClick = { }
                    )
                }
                item {
                    HomeFeatureCard(
                        title = "Habit Tracker",
                        description = "Maintain your daily rhythms.",
                        icon = Icons.Default.CalendarMonth,
                        actionText = "Check-in",
                        onClick = onNavigateToHabits
                    )
                }
            }
        }
    }
}

@Composable
fun HomeTopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.Settings, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(24.dp))
        Text(
            text = "Adhera",
            style = MaterialTheme.typography.titleLarge,
            color = NavyPrimary,
            fontWeight = FontWeight.Bold
        )
        Icon(Icons.Default.Settings, contentDescription = null, tint = NavyPrimary)
    }
}

@Composable
fun HomeFeatureCard(
    title: String,
    description: String,
    icon: ImageVector,
    actionText: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = NavyPrimary,
                modifier = Modifier.size(28.dp)
            )

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = NavyPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = actionText,
                    style = MaterialTheme.typography.labelLarge,
                    color = NavyPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    Icons.AutoMirrored.Filled.ArrowForwardIos,
                    contentDescription = null,
                    tint = NavyPrimary,
                    modifier = Modifier.size(10.dp)
                )
            }
        }
    }
}
