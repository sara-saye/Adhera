package com.gpproject.adhera.treatment.treatment.habit_tracker.stats

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gpproject.adhera.treatment.treatment.habit_tracker.Habit
import com.gpproject.adhera.treatment.treatment.habit_tracker.HabitTrackerAccent
import com.gpproject.adhera.treatment.treatment.habit_tracker.HabitTrackerBackground
import com.gpproject.adhera.treatment.treatment.habit_tracker.HabitTrackerCard
import com.gpproject.adhera.treatment.treatment.habit_tracker.HabitTrackerInk
import com.gpproject.adhera.treatment.treatment.habit_tracker.HabitTrackerMuted
import com.gpproject.adhera.treatment.treatment.habit_tracker.HabitTrackerSoft
import com.gpproject.adhera.treatment.treatment.habit_tracker.habitdb.HabitServiceLocator
import com.gpproject.adhera.ui.screens.treatment.habit_tracker.AdheraViewModelFactory

@Composable
fun PerformanceAnalyticsScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = remember { HabitServiceLocator.getRepository(context) }
    val viewModel: StatsViewModel = viewModel(
        factory = AdheraViewModelFactory(repository)
    )

    val habits by viewModel.habitsList.collectAsState()
    val analytics by viewModel.analyticsState.collectAsState()

    Scaffold(
        containerColor = HabitTrackerBackground,
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                AnalyticsSummaryCard(analytics)
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = HabitTrackerCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Weekly consistency", color = HabitTrackerInk, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            listOf(0.9f, 0.72f, 0.56f, 0.82f, 0.42f, 0.28f, 0.35f).forEachIndexed { index, value ->
                                DayBar(index = index, value = value)
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Habit progress",
                    color = HabitTrackerInk,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            if (habits.isEmpty()) {
                item {
                    EmptyProgressCard()
                }
            } else {
                items(habits) { habit ->
                    HabitProgressRow(habit)
                }
            }
        }
    }
}

@Composable
private fun AnalyticsSummaryCard(analytics: AnalyticsData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = HabitTrackerCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Today overview",
                color = HabitTrackerInk,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )
            val progress by animateFloatAsState(
                targetValue = analytics.efficiencyPercentage.coerceIn(0, 100) / 100f,
                label = "habit-completion"
            )
            Box(contentAlignment = Alignment.Center, modifier = Modifier.size(124.dp)) {
                Canvas(modifier = Modifier.size(124.dp)) {
                    drawCircle(
                        color = HabitTrackerSoft,
                        style = Stroke(width = 12.dp.toPx())
                    )
                    drawArc(
                        color = HabitTrackerAccent,
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        style = Stroke(width = 12.dp.toPx(), cap = StrokeCap.Round)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${analytics.efficiencyPercentage}%",
                        color = HabitTrackerInk,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineSmall
                    )
                    Text("completed", color = HabitTrackerMuted, fontSize = 12.sp)
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Metric("Active habits", analytics.activeHabitsCount.toString())
                Metric("Daily average", String.format("%.1f", analytics.dailyAverage))
            }
        }
    }
}

@Composable
private fun Metric(label: String, value: String) {
    Column {
        Text(label, color = HabitTrackerMuted, fontSize = 12.sp)
        Text(value, color = HabitTrackerInk, fontWeight = FontWeight.Bold, fontSize = 20.sp)
    }
}

@Composable
private fun DayBar(index: Int, value: Float) {
    val labels = listOf("M", "T", "W", "T", "F", "S", "S")
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(width = 28.dp, height = 76.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(HabitTrackerSoft),
            contentAlignment = Alignment.BottomCenter
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height((76 * value).dp)
                    .background(HabitTrackerAccent)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(labels[index], color = HabitTrackerMuted, fontSize = 11.sp)
    }
}

@Composable
private fun HabitProgressRow(habit: Habit) {
    val progress = if (habit.isCompletedToday) 1f else 0.35f
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = HabitTrackerCard)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(habit.name, color = HabitTrackerInk, fontWeight = FontWeight.SemiBold)
                Text(if (habit.isCompletedToday) "Done" else "Open", color = HabitTrackerMuted, fontSize = 12.sp)
            }
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = HabitTrackerAccent,
                trackColor = HabitTrackerSoft
            )
        }
    }
}

@Composable
private fun EmptyProgressCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = HabitTrackerCard)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text("No habits yet", color = HabitTrackerInk, fontWeight = FontWeight.Bold)
            Text(
                "Add one habit to start seeing progress here.",
                color = HabitTrackerMuted,
                fontSize = 13.sp
            )
        }
    }
}
