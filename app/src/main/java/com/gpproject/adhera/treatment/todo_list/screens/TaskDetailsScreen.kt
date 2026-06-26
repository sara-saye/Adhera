package com.gpproject.adhera.treatment.todo_list.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gpproject.adhera.ui.theme.NavyPrimary

@Composable
fun TaskDetailsScreen(
    taskId: String,
    viewModel: com.gpproject.adhera.treatment.todo_list.screens.TaskViewModel,
    onBack: () -> Unit,
    onEdit: (String) -> Unit
) {
    LaunchedEffect(taskId) {
        viewModel.loadTaskById(taskId)
    }

    val currentTask by viewModel.currentTaskState.collectAsState()

    DisposableEffect(Unit) {
        onDispose { viewModel.clearCurrentTask() }
    }

    val task = currentTask
    if (task == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(_root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.TodoBackground),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = NavyPrimary)
        }
        return
    }

    Scaffold(containerColor = _root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.TodoBackground) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(bottom = 28.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back", tint = NavyPrimary)
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(task.title, color = NavyPrimary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall)
                        Text(task.durationType, color = _root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.TodoMuted, fontSize = 13.sp)
                    }
                    TextButton(onClick = { onEdit(task.id) }) {
                        Text("Edit", color = NavyPrimary, fontWeight = FontWeight.Bold)
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    border = BorderStroke(1.dp, Color(0xFFE6EAF0))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        _root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.DetailRow(
                            "Start",
                            "${task.startDate} at ${task.startTime}"
                        )
                        if (!task.endDate.isNullOrBlank()) _root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.DetailRow(
                            "End",
                            task.endDate
                        )
                        _root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.DetailRow(
                            "Daily focus",
                            task.dailyFocus
                        )
                        _root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.DetailRow(
                            "Priority",
                            task.priority
                        )
                    }
                }
            }

            item {
                _root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.SectionTitle("Description")
                Text(
                    text = task.description.ifBlank { "No description added." },
                    color = _root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.TodoMuted,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }

            if (task.milestones.isNotEmpty()) {
                item {
                    _root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.SectionTitle(
                        "Steps"
                    )
                }
                itemsIndexed(task.milestones) { index, step ->
                    _root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.StepRow(
                        number = index + 1,
                        text = step
                    )
                }
            }

            if (task.reminderEnabled) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, Color(0xFFE6EAF0))
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.NotificationsNone, contentDescription = null, tint = NavyPrimary)
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("Reminder enabled", color = NavyPrimary, fontWeight = FontWeight.Bold)
                                Text("5 minutes before ${task.startTime}", color = _root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.TodoMuted, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = _root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.TodoMuted, fontSize = 13.sp)
        Text(value.orEmpty(), color = NavyPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
private fun StepRow(number: Int, text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, Color(0xFFE6EAF0))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(NavyPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(number.toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
            Spacer(Modifier.width(12.dp))
            Text(text, color = NavyPrimary, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, color = NavyPrimary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
}
