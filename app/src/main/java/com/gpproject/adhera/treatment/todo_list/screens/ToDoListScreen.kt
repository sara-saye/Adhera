package com.gpproject.adhera.treatment.todo_list.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gpproject.adhera.treatment.todo_list.tododb.TaskEntity
import com.gpproject.adhera.ui.theme.NavyPrimary

@Composable
fun TodoListScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onNavigateToDetails: (String) -> Unit,
    onBack: () -> Unit,
    initialTab: Int = 0,
    viewModel: com.gpproject.adhera.treatment.todo_list.screens.TaskViewModel = viewModel()
) {
    var selectedTab by remember { mutableIntStateOf(initialTab) }
    val allTasks by viewModel.tasksState.collectAsState()
    val tabs = listOf("Today", "Week", "Month", "All")

    val filteredTasks = remember(allTasks, selectedTab) {
        allTasks.filter { task ->
            when (selectedTab) {
                0 -> task.durationType == "Today"
                1 -> task.durationType == "Week"
                2 -> task.durationType == "Month"
                else -> true
            }
        }
    }

    Scaffold(
        containerColor = _root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.TodoBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = NavyPrimary,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create task")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp, bottom = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back", tint = NavyPrimary)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Tasks", color = NavyPrimary, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall)
                    Text("${allTasks.count { !it.isCompleted }} open tasks", color = _root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.TodoMuted, fontSize = 13.sp)
                }
                TextButton(onClick = { viewModel.clearCompletedTasks() }) {
                    Text("Clear done", color = NavyPrimary, fontWeight = FontWeight.SemiBold)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                tabs.forEachIndexed { index, label ->
                    FilterChip(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        label = { Text(label) }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 88.dp)
            ) {
                if (filteredTasks.isEmpty()) {
                    item {
                        _root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.EmptyTasksCard(
                            onNavigateToCreate
                        )
                    }
                } else {
                    items(filteredTasks, key = { it.id }) { task ->
                        _root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.TaskItem(
                            task = task,
                            onToggleClick = { viewModel.toggleTaskCompletion(task) },
                            onItemClick = { onNavigateToDetails(task.id) },
                            onEditClick = { onNavigateToEdit(task.id) },
                            onDeleteClick = { viewModel.deleteTask(task) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    task: com.gpproject.adhera.treatment.todo_list.tododb.TaskEntity,
    onToggleClick: () -> Unit,
    onItemClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val priorityColor = when (task.priority) {
        "High" -> Color(0xFFC2410C)
        "Medium" -> Color(0xFFB7791F)
        else -> Color(0xFF2F855A)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE6EAF0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(if (task.isCompleted) NavyPrimary else Color.White, CircleShape)
                    .clickable { onToggleClick() },
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Icon(Icons.Default.Check, contentDescription = "Done", tint = Color.White, modifier = Modifier.size(18.dp))
                } else {
                    Box(modifier = Modifier.size(28.dp).background(Color.Transparent, CircleShape))
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = task.title,
                    color = NavyPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                )
                Text(
                    text = "${task.durationType} • ${task.startTime} • ${task.dailyFocus}",
                    color = _root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.TodoMuted,
                    fontSize = 12.sp
                )
                Surface(
                    color = priorityColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(999.dp)
                ) {
                    Text(
                        task.priority,
                        color = priorityColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
            IconButton(onClick = onEditClick) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = NavyPrimary)
            }
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.DeleteOutline, contentDescription = "Delete", tint = Color(0xFFC62828))
            }
        }
    }
}

@Composable
private fun EmptyTasksCard(onCreate: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("No tasks here", color = NavyPrimary, fontWeight = FontWeight.Bold)
            Text("Create one task and keep the list light.", color = _root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.TodoMuted, fontSize = 13.sp)
            TextButton(onClick = onCreate) {
                Text("Create task", color = NavyPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

internal val TodoBackground = Color(0xFFF8FAFB)
internal val TodoMuted = Color(0xFF687782)
