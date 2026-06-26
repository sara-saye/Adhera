package com.gpproject.adhera.treatment.habit_tracker.habits

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.gpproject.adhera.navigation.HabitTrackerAccent
import com.gpproject.adhera.navigation.HabitTrackerBackground
import com.gpproject.adhera.navigation.HabitTrackerCard
import com.gpproject.adhera.navigation.HabitTrackerInk
import com.gpproject.adhera.navigation.HabitTrackerMuted
import com.gpproject.adhera.navigation.HabitTrackerSoft
import com.gpproject.adhera.treatment.habit_tracker.habitdb.HabitServiceLocator
import com.gpproject.adhera.ui.screens.treatment.habit_tracker.AdheraViewModelFactory
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun NewHabitScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val repository = remember { HabitServiceLocator.getRepository(context) }
    val viewModel: NewHabitViewModel = viewModel(
        factory = AdheraViewModelFactory(repository)
    )

    val categories = listOf("Mind", "Body", "Focus", "Finance", "Social")
    val colors = listOf("#102A43", "#2E7D8F", "#7C3AED", "#C2410C", "#166534")
    val days = listOf("M", "T", "W", "F", "S")

    Scaffold(
        containerColor = HabitTrackerBackground,
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = HabitTrackerCard),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "New habit",
                            color = HabitTrackerInk,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )

                        OutlinedTextField(
                            value = viewModel.habitName,
                            onValueChange = { viewModel.habitName = it },
                            label = { Text("Habit name") },
                            placeholder = { Text("Morning walk") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = HabitTrackerAccent,
                                unfocusedBorderColor = HabitTrackerMuted.copy(alpha = 0.35f)
                            )
                        )

                        Text("Category", color = HabitTrackerInk, fontWeight = FontWeight.SemiBold)
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            categories.forEach { category ->
                                FilterChip(
                                    selected = viewModel.selectedCategory == category,
                                    onClick = { viewModel.selectedCategory = category },
                                    label = { Text(category) }
                                )
                            }
                        }

                        Text("Color", color = HabitTrackerInk, fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            colors.forEach { hex ->
                                val color = Color(android.graphics.Color.parseColor(hex))
                                val selected = viewModel.selectedColorHex == hex
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .border(
                                            width = if (selected) 3.dp else 1.dp,
                                            color = if (selected) HabitTrackerInk else HabitTrackerSoft,
                                            shape = CircleShape
                                        )
                                        .clickable { viewModel.selectedColorHex = hex }
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Reminder", color = HabitTrackerInk, fontWeight = FontWeight.SemiBold)
                                Text(viewModel.reminderTime, color = HabitTrackerMuted, fontSize = 13.sp)
                            }
                            Button(
                                onClick = {
                                    val calendar = Calendar.getInstance()
                                    TimePickerDialog(
                                        context,
                                        { _, hour, minute ->
                                            val format = if (hour >= 12) "PM" else "AM"
                                            val displayHour = if (hour % 12 == 0) 12 else hour % 12
                                            viewModel.reminderTime = String.format(
                                                Locale.US,
                                                "%02d:%02d %s",
                                                displayHour,
                                                minute,
                                                format
                                            )
                                        },
                                        calendar.get(Calendar.HOUR_OF_DAY),
                                        calendar.get(Calendar.MINUTE),
                                        false
                                    ).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = HabitTrackerAccent),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Change")
                            }
                        }

                        Text("Repeat", color = HabitTrackerInk, fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            days.forEach { day ->
                                val selected = viewModel.selectedDays.contains(day)
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(if (selected) HabitTrackerInk else HabitTrackerSoft)
                                        .clickable { viewModel.toggleDay(day) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = day,
                                        color = if (selected) Color.White else HabitTrackerInk,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Button(
                            onClick = { viewModel.saveHabit(onSuccess = onBackClick) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = HabitTrackerInk),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Save habit")
                        }
                    }
                }
            }

            item {
                val previewColor = Color(android.graphics.Color.parseColor(viewModel.selectedColorHex))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = previewColor)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = viewModel.selectedCategory,
                            color = Color.White.copy(alpha = 0.82f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = viewModel.habitName.ifBlank { "Your next habit" },
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Text(
                            text = "Reminder at ${viewModel.reminderTime}",
                            color = Color.White.copy(alpha = 0.82f),
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}
