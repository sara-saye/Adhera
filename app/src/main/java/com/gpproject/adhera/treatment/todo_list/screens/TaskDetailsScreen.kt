package com.gpproject.adhera.ui.screens.treatment.todo_list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gpproject.adhera.treatment.todo_list.screens.TaskViewModel
import com.gpproject.adhera.ui.theme.*

@Composable
fun TaskDetailsScreen(
    taskId: String,
    viewModel: TaskViewModel,
    onBack: () -> Unit,
    onEdit: (String) -> Unit
) {
    // تحميل بيانات التاسك الحقيقية
    LaunchedEffect(taskId) {
        viewModel.loadTaskById(taskId)
    }

    val currentTask by viewModel.currentTaskState.collectAsStateWithLifecycle()

    // Loading state
    if (currentTask == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = NavyPrimary)
        }
        return
    }

    val task = currentTask!!
    val isRangeTask = task.endDate != null
    val dateValue = if (isRangeTask) "${task.startDate} - ${task.endDate}" else task.startDate

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Default.ArrowBackIosNew,
                        contentDescription = null,
                        tint = NavyPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Text(
                    text = task.title,
                    fontSize = 20.sp,
                    color = NavyPrimary,
                    fontWeight = FontWeight.ExtraBold
                )
                IconButton(onClick = { onEdit(taskId) }) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = null,
                        tint = NavyPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        },
        containerColor = Color.White
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(bottom = 40.dp)
        ) {
            // Stats Grid بالبيانات الحقيقية
            item {
                TaskStatsGrid(
                    dateValue = dateValue,
                    priority = task.priority,
                    dailyFocus = task.dailyFocus,
                    isRange = isRangeTask
                )
            }

            // Progress bar للـ range tasks
            if (isRangeTask) {
                item {
                    TaskDeadlineProgress(progress = 0.65f, daysLeft = 3)
                }
            }

            // Description
            if (task.description.isNotBlank()) {
                item {
                    TaskSectionHeading("Description")
                    Text(
                        text = task.description,
                        color = NavyPrimary.copy(alpha = 0.7f),
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            // Milestones / Sub-tasks
            if (task.milestones.isNotEmpty()) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TaskSectionHeading("Plan Breakdown")
                        Surface(
                            color = Color(0xFFFDE7E7),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                "${task.milestones.size} Tasks",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = Color.Red,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                itemsIndexed(task.milestones) { index, milestone ->
                    MilestoneItem(index = index, text = milestone, total = task.milestones.size)
                }
            }

            // Reminder
            if (task.reminderEnabled) {
                item {
                    TaskReminderCard("5 mins before focus hours")
                }
            }
        }
    }
}

// ─── Helper Composables ───────────────────────────────────────────────────────

@Composable
fun TaskStatsGrid(
    dateValue: String,
    priority: String,
    dailyFocus: String,
    isRange: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFF7F9FC)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                TaskStatItem(
                    label = if (isRange) "DATE RANGE" else "START DATE",
                    value = dateValue,
                    icon = Icons.Default.CalendarToday,
                    modifier = Modifier.weight(1f)
                )
                TaskStatItem(
                    label = "PRIORITY",
                    value = priority,
                    icon = Icons.Default.Speed,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                TaskStatItem(
                    label = "DAILY FOCUS",
                    value = dailyFocus,
                    icon = Icons.Default.AccessTime,
                    modifier = Modifier.weight(1f)
                )
                if (isRange) {
                    TaskStatItem(
                        label = "COMPLETION",
                        value = "65%",
                        icon = Icons.Default.DonutLarge,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun TaskStatItem(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(
            label,
            fontSize = 11.sp,
            color = NavyPrimary.copy(alpha = 0.5f),
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = NavyPrimary
            )
            Spacer(Modifier.width(6.dp))
            Text(
                value,
                fontSize = 16.sp,
                color = NavyPrimary,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
fun MilestoneItem(index: Int, text: String, total: Int) {
    // أول 2 = completed، اللي بعده = in progress، الباقي = upcoming
    val status = when {
        index < 2              -> MilestoneStatus.COMPLETED
        index == 2             -> MilestoneStatus.IN_PROGRESS
        else                   -> MilestoneStatus.UPCOMING
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (status == MilestoneStatus.IN_PROGRESS) Color.White else Color(0xFFF7F9FC),
        border = if (status == MilestoneStatus.IN_PROGRESS)
            androidx.compose.foundation.BorderStroke(2.dp, NavyPrimary)
        else null
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(
                        if (status == MilestoneStatus.COMPLETED) NavyPrimary else Color.White,
                        CircleShape
                    )
                    .border(
                        1.5.dp,
                        if (status == MilestoneStatus.UPCOMING) Color.LightGray else NavyPrimary,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                when (status) {
                    MilestoneStatus.COMPLETED   ->
                        Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(18.dp))
                    MilestoneStatus.IN_PROGRESS ->
                        Box(Modifier.size(10.dp).background(NavyPrimary, CircleShape))
                    MilestoneStatus.UPCOMING    -> {}
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text,
                    fontWeight = FontWeight.Bold,
                    color = if (status == MilestoneStatus.UPCOMING) Color.Gray else NavyPrimary,
                    fontSize = 15.sp
                )
                Text(
                    text = when (status) {
                        MilestoneStatus.COMPLETED   -> "Completed"
                        MilestoneStatus.IN_PROGRESS -> "In Progress"
                        MilestoneStatus.UPCOMING    -> "Upcoming"
                    },
                    fontSize = 12.sp,
                    color = NavyPrimary.copy(alpha = 0.5f)
                )
            }
            if (status == MilestoneStatus.IN_PROGRESS) {
                Icon(
                    Icons.Default.PlayCircleFilled,
                    null,
                    tint = NavyPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun TaskReminderCard(time: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFFF7F9FC)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.Notifications,
                    null,
                    tint = NavyPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    "Reminder: Enabled",
                    fontWeight = FontWeight.ExtraBold,
                    color = NavyPrimary,
                    fontSize = 16.sp
                )
                Text(text = time, fontSize = 13.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun TaskDeadlineProgress(progress: Float, daysLeft: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Progress to deadline",
                fontSize = 13.sp,
                color = NavyPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                "$daysLeft Days left",
                fontSize = 13.sp,
                color = NavyPrimary,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(10.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp),
            color = NavyPrimary,
            trackColor = Color(0xFFF0F0F0),
            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
        )
    }
}

@Composable
fun TaskSectionHeading(text: String) {
    Text(text, fontSize = 22.sp, color = NavyPrimary, fontWeight = FontWeight.Black)
}

enum class MilestoneStatus { COMPLETED, IN_PROGRESS, UPCOMING }