package com.gpproject.adhera.treatment.todo_list.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gpproject.adhera.treatment.todo_list.tododb.TaskEntity
import com.gpproject.adhera.ui.theme.NavyPrimary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.UUID

@Composable
fun CreateTaskScreen(
    onBack: () -> Unit,
    initialTab: String = "Today",
    forceDatePicker: Boolean = false,
    forceTimePicker: Boolean = false,
    viewModel: com.gpproject.adhera.treatment.todo_list.screens.TaskViewModel
) {
    val context = LocalContext.current
    var taskTitle by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedDuration by remember { mutableStateOf(initialTab) }
    var selectedPriority by remember { mutableStateOf("Medium") }
    var reminderEnabled by remember { mutableStateOf(true) }
    var startDateText by remember { mutableStateOf(
        _root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.formatDate(
            Calendar.getInstance()
        )
    ) }
    var endDateText by remember { mutableStateOf("") }
    var startTimeText by remember { mutableStateOf(
        _root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.formatTime(
            Calendar.getInstance()
        )
    ) }
    var dailyFocusText by remember { mutableStateOf("02:00 HRS") }
    val milestones = remember { mutableStateListOf<String>() }
    val isGenerating by viewModel.isGeneratingMilestones.collectAsState()
    val aiError by viewModel.aiErrorState.collectAsState()

    LaunchedEffect(forceDatePicker) {
        if (forceDatePicker) _root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.showDatePicker(
            context
        ) { startDateText = it }
    }
    LaunchedEffect(forceTimePicker) {
        if (forceTimePicker) _root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.showTimePicker(
            context
        ) { startTimeText = it }
    }

    Scaffold(
        containerColor = _root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.TodoBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back", tint = NavyPrimary)
                }
                Text("Create task", color = NavyPrimary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall)
            }

            _root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.SectionLabel("Duration")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Today", "Week", "Month", "Custom").forEach { duration ->
                    FilterChip(
                        selected = selectedDuration == duration,
                        onClick = { selectedDuration = duration },
                        label = { Text(duration) }
                    )
                }
            }

            OutlinedTextField(
                value = taskTitle,
                onValueChange = { taskTitle = it },
                label = { Text("Task title") },
                placeholder = { Text("Write report") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                placeholder = { Text("Add enough detail for planning") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp)
            )

            _root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.SectionLabel("Schedule")
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                _root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.SmallActionCard(
                    label = "Start date",
                    value = startDateText,
                    icon = Icons.Default.CalendarMonth,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        _root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.showDatePicker(
                            context
                        ) { startDateText = it }
                    }
                )
                _root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.SmallActionCard(
                    label = "Start time",
                    value = startTimeText,
                    icon = Icons.Default.Schedule,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        _root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.showTimePicker(
                            context
                        ) { startTimeText = it }
                    }
                )
            }

            if (selectedDuration == "Custom") {
                _root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.SmallActionCard(
                    label = "End date",
                    value = endDateText.ifBlank { "Select end date" },
                    icon = Icons.Default.CalendarMonth,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        _root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.showDatePicker(
                            context
                        ) { endDateText = it }
                    }
                )
            }

            OutlinedTextField(
                value = dailyFocusText,
                onValueChange = { dailyFocusText = it },
                label = { Text("Daily focus") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            _root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.SectionLabel("Priority")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("High", "Medium", "Low").forEach { priority ->
                    FilterChip(
                        selected = selectedPriority == priority,
                        onClick = { selectedPriority = priority },
                        label = { Text(priority) }
                    )
                }
            }

            if (selectedDuration != "Today") {
                _root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.AiPlannerCard(
                    isGenerating = isGenerating,
                    canGenerate = taskTitle.isNotBlank() && description.length >= 12,
                    error = aiError,
                    onGenerate = {
                        viewModel.generateAiMilestones(taskTitle, description) { steps ->
                            milestones.clear()
                            milestones.addAll(steps)
                        }
                    },
                    onAddStep = { milestones.add("") }
                )

                milestones.forEachIndexed { index, step ->
                    OutlinedTextField(
                        value = step,
                        onValueChange = { milestones[index] = it },
                        label = { Text("Step ${index + 1}") },
                        trailingIcon = {
                            IconButton(onClick = { milestones.removeAt(index) }) {
                                Icon(Icons.Default.Close, contentDescription = "Remove step")
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE6EAF0))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.NotificationsActive, contentDescription = null, tint = NavyPrimary)
                    Spacer(Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Reminder", color = NavyPrimary, fontWeight = FontWeight.Bold)
                        Text("5 minutes before start time", color = _root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.TodoMuted, fontSize = 12.sp)
                    }
                    Switch(checked = reminderEnabled, onCheckedChange = { reminderEnabled = it })
                }
            }

            Button(
                onClick = {
                    val task =
                        _root_ide_package_.com.gpproject.adhera.treatment.todo_list.tododb.TaskEntity(
                            id = UUID.randomUUID().toString(),
                            title = taskTitle.trim(),
                            description = description.trim(),
                            durationType = selectedDuration,
                            startDate = startDateText,
                            endDate = if (selectedDuration == "Custom") endDateText.ifBlank { null } else null,
                            startTime = startTimeText,
                            dailyFocus = dailyFocusText,
                            priority = selectedPriority,
                            reminderEnabled = reminderEnabled,
                            milestones = milestones.map { it.trim() }.filter { it.isNotBlank() }
                        )
                    viewModel.upsertTask(task) { onBack() }
                },
                enabled = taskTitle.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save task", fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun AiPlannerCard(
    isGenerating: Boolean,
    canGenerate: Boolean,
    error: String?,
    onGenerate: () -> Unit,
    onAddStep: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = NavyPrimary)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("AI steps", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Gemini can split this task into smaller actions.", color = Color.White.copy(alpha = 0.78f), fontSize = 12.sp)
                }
                Button(
                    onClick = onGenerate,
                    enabled = canGenerate && !isGenerating,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = NavyPrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    if (isGenerating) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = NavyPrimary)
                    } else {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Generate")
                    }
                }
            }
            TextButton(onClick = onAddStep) {
                Text("Add step manually", color = Color.White, fontWeight = FontWeight.Bold)
            }
            error?.let {
                Text(it, color = Color(0xFFFFCDD2), fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, color = NavyPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
}

@Composable
private fun SmallActionCard(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE6EAF0)),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = NavyPrimary)
            Spacer(Modifier.width(10.dp))
            Column {
                Text(label, color = _root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.TodoMuted, fontSize = 11.sp)
                Text(value, color = NavyPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}

private fun showDatePicker(context: android.content.Context, onSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    DatePickerDialog(
        context,
        { _, year, month, day ->
            val selected = Calendar.getInstance().apply { set(year, month, day) }
            onSelected(
                _root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.formatDate(
                    selected
                )
            )
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}

private fun showTimePicker(context: android.content.Context, onSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    TimePickerDialog(
        context,
        { _, hour, minute ->
            val selected = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
            }
            onSelected(
                _root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.formatTime(
                    selected
                )
            )
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        false
    ).show()
}

private fun formatDate(calendar: Calendar): String {
    return SimpleDateFormat("MMM dd, yyyy", Locale.US).format(calendar.time)
}

private fun formatTime(calendar: Calendar): String {
    return SimpleDateFormat("hh:mm a", Locale.US).format(calendar.time)
}
