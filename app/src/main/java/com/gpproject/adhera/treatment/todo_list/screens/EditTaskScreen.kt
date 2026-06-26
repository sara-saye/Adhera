package com.gpproject.adhera.treatment.todo_list.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
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
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gpproject.adhera.ui.theme.NavyPrimary

@Composable
fun EditTaskScreen(
    taskId: String = "1",
    taskTitleDefault: String = "Hello Deep Work",
    hasSubTasks: Boolean = true,
    onBack: () -> Unit,
    viewModel: com.gpproject.adhera.treatment.todo_list.screens.TaskViewModel
) {
    LaunchedEffect(taskId) {
        viewModel.loadTaskById(taskId)
    }

    val currentTask by viewModel.currentTaskState.collectAsState()
    val isGenerating by viewModel.isGeneratingMilestones.collectAsState()
    val aiError by viewModel.aiErrorState.collectAsState()

    var title by remember { mutableStateOf(taskTitleDefault) }
    var description by remember { mutableStateOf("") }
    var priority by remember { mutableStateOf("Medium") }
    var reminderEnabled by remember { mutableStateOf(true) }
    val milestones = remember { mutableStateListOf<String>() }

    LaunchedEffect(currentTask) {
        currentTask?.let { task ->
            title = task.title
            description = task.description
            priority = task.priority
            reminderEnabled = task.reminderEnabled
            milestones.clear()
            milestones.addAll(task.milestones)
        }
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.clearCurrentTask() }
    }

    val task = currentTask
    if (task == null) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = NavyPrimary)
        }
        return
    }

    Scaffold(containerColor = _root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.TodoBackground) { padding ->
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
                Text("Edit task", color = NavyPrimary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall)
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(12.dp)
            )

            Text("Priority", color = NavyPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("High", "Medium", "Low").forEach { option ->
                    FilterChip(
                        selected = priority == option,
                        onClick = { priority = option },
                        label = { Text(option) }
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE6EAF0))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Schedule", color = NavyPrimary, fontWeight = FontWeight.Bold)
                    Text("${task.startDate} at ${task.startTime}", color = _root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.TodoMuted, fontSize = 13.sp)
                    Text("Daily focus: ${task.dailyFocus}", color = _root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.TodoMuted, fontSize = 13.sp)
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

            if (hasSubTasks) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Steps", color = NavyPrimary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Row {
                        TextButton(onClick = { milestones.add("") }) {
                            Text("Add", color = NavyPrimary, fontWeight = FontWeight.Bold)
                        }
                        TextButton(
                            onClick = {
                                viewModel.generateAiMilestones(title, description) { steps ->
                                    milestones.clear()
                                    milestones.addAll(steps)
                                }
                            },
                            enabled = !isGenerating && title.isNotBlank()
                        ) {
                            if (isGenerating) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = NavyPrimary)
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("AI", color = NavyPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                aiError?.let { Text(it, color = Color(0xFFC62828), fontSize = 12.sp) }

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

            Button(
                onClick = {
                    val updatedTask = task.copy(
                        title = title.trim(),
                        description = description.trim(),
                        priority = priority,
                        reminderEnabled = reminderEnabled,
                        milestones = milestones.map { it.trim() }.filter { it.isNotBlank() }
                    )
                    viewModel.upsertTask(updatedTask) { onBack() }
                },
                enabled = title.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save changes", fontWeight = FontWeight.Bold)
            }

            TextButton(
                onClick = { viewModel.deleteTask(task) { onBack() } },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = Color(0xFFC62828))
                Spacer(Modifier.width(6.dp))
                Text("Delete task", color = Color(0xFFC62828), fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
