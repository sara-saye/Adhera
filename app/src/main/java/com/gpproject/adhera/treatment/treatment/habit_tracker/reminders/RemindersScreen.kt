package com.gpproject.adhera.treatment.treatment.habit_tracker.reminders

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gpproject.adhera.treatment.treatment.habit_tracker.HabitTrackerAccent
import com.gpproject.adhera.treatment.treatment.habit_tracker.HabitTrackerBackground
import com.gpproject.adhera.treatment.treatment.habit_tracker.HabitTrackerCard
import com.gpproject.adhera.treatment.treatment.habit_tracker.HabitTrackerInk
import com.gpproject.adhera.treatment.treatment.habit_tracker.HabitTrackerMuted
import com.gpproject.adhera.treatment.treatment.habit_tracker.HabitTrackerSoft
import com.gpproject.adhera.treatment.treatment.habit_tracker.habitdb.HabitServiceLocator
import com.gpproject.adhera.ui.screens.treatment.habit_tracker.AdheraViewModelFactory
import java.util.Calendar
import java.util.Locale

@Composable
fun RemindersScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = remember { HabitServiceLocator.getRepository(context) }
    val viewModel: RemindersViewModel = viewModel(
        factory = AdheraViewModelFactory(repository)
    )
    val activeCount by viewModel.activeHabitsCount.collectAsState()

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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Reminder plan",
                            color = HabitTrackerInk,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "$activeCount active habits",
                            color = HabitTrackerMuted,
                            fontSize = 13.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .background(HabitTrackerSoft)
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                    ) {
                        Text("Today", color = HabitTrackerInk, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                }
            }

            item {
                ReminderCard(
                    title = "Hydration",
                    subtitle = "Gentle check-ins through the day",
                    checked = viewModel.isHydrationEnabled,
                    onCheckedChange = viewModel::toggleHydration
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        viewModel.hydrationSlots.take(3).forEach { time ->
                            TimeSlot(time = time, modifier = Modifier.weight(1f))
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Add time",
                        color = HabitTrackerAccent,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable {
                            val calendar = Calendar.getInstance()
                            TimePickerDialog(
                                context,
                                { _, hour, minute ->
                                    val format = if (hour >= 12) "PM" else "AM"
                                    val displayHour = if (hour % 12 == 0) 12 else hour % 12
                                    viewModel.addHydrationSlot(
                                        String.format(Locale.US, "%02d:%02d %s", displayHour, minute, format)
                                    )
                                },
                                calendar.get(Calendar.HOUR_OF_DAY),
                                calendar.get(Calendar.MINUTE),
                                false
                            ).show()
                        }
                    )
                }
            }

            item {
                ReminderCard(
                    title = "Mindful minutes",
                    subtitle = "Short reset prompts during long sessions",
                    checked = viewModel.isMindfulMinutesEnabled,
                    onCheckedChange = { viewModel.isMindfulMinutesEnabled = it }
                ) {
                    Text("Next reminder: 10:30 AM", color = HabitTrackerMuted, fontSize = 13.sp)
                }
            }

            item {
                ReminderCard(
                    title = "Deep work block",
                    subtitle = "A single quiet work window",
                    checked = viewModel.isDeepWorkEnabled,
                    onCheckedChange = { viewModel.isDeepWorkEnabled = it }
                ) {
                    Text("Weekdays at 08:00 AM", color = HabitTrackerMuted, fontSize = 13.sp)
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = HabitTrackerInk)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Daily summary", color = Color.White, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (viewModel.isMorningSummary) "Morning recap" else "Evening recap",
                                color = Color.White.copy(alpha = 0.78f),
                                fontSize = 13.sp
                            )
                            Switch(
                                checked = viewModel.isMorningSummary,
                                onCheckedChange = { viewModel.isMorningSummary = it },
                                colors = SwitchDefaults.colors(checkedTrackColor = HabitTrackerAccent)
                            )
                        }
                        HorizontalDivider(color = Color.White.copy(alpha = 0.16f))
                        Text(viewModel.summaryTime, color = Color.White, style = MaterialTheme.typography.headlineSmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReminderCard(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, color = HabitTrackerInk, fontWeight = FontWeight.Bold)
                    Text(subtitle, color = HabitTrackerMuted, fontSize = 12.sp, lineHeight = 16.sp)
                }
                Switch(
                    checked = checked,
                    onCheckedChange = onCheckedChange,
                    colors = SwitchDefaults.colors(checkedTrackColor = HabitTrackerAccent)
                )
            }
            content()
        }
    }
}

@Composable
private fun TimeSlot(time: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(36.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(HabitTrackerSoft),
        contentAlignment = Alignment.Center
    ) {
        Text(time, color = HabitTrackerInk, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
    }
}
