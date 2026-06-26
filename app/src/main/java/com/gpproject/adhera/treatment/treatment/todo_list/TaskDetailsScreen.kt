package com.gpproject.adhera.treatment.treatment.todo_list

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gpproject.adhera.ui.theme.*

@Composable
fun TaskDetailsScreen(
    taskId: String,
    viewModel: TaskViewModel,
    onBack: () -> Unit,
    onEdit: (String) -> Unit
) {
    // 1. طلب جلب بيانات المهمة المحددة أول ما السكرين تفتح
    LaunchedEffect(taskId) {
        viewModel.loadTaskById(taskId)
    }

    // 2. مراقبة الـ State الخاصة بالمهمة الحالية
    val currentTask by viewModel.currentTaskState.collectAsState()

    // تنظيف البيانات عند الخروج من الشاشة لضمان عدم حدوث وميض (Flicker) بالبيانات القديمة المرة القادمة
    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearCurrentTask()
        }
    }

    // 3. حالة الانتظار حتى تحميل البيانات من الـ Room Database
    val task = currentTask
    if (task == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = NavyPrimary)
        }
    } else {
        // ربط المتغيرات بالبيانات الحقيقية القادمة من الموديل
        val taskTitle = task.title
        val dateValue = if (task.durationType == "Week" || task.durationType == "Month" || task.durationType == "Custom") {
            if (task.endDate != null) "${task.startDate} - ${task.endDate}" else task.startDate
        } else {
            task.startDate
        }
        val onSettings = { onEdit(taskId) }
        val isRangeTask = task.durationType == "Week" || task.durationType == "Month" || task.durationType == "Custom"

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
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(22.dp))
                    }
                    Text(
                        text = taskTitle,
                        fontSize = 20.sp,
                        color = NavyPrimary,
                        fontWeight = FontWeight.ExtraBold
                    )
                    IconButton(onClick = onSettings) {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(26.dp))
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
                // باصينا الـ dateValue الديناميكي والـ priority والـ focus
                item {
                    StatsGrid(
                        dateValue = dateValue,
                        priority = task.priority,
                        dailyFocus = task.dailyFocus
                    )
                }

                if (isRangeTask) {
                    item { DeadlineProgress(progress = 0.65f, daysLeft = 3) }
                }

                item {
                    SectionHeading("Description")
                    Text(
                        text = task.description.ifEmpty { "No description provided for this task." },
                        color = NavyPrimary.copy(alpha = 0.7f),
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                // الـ Plan Breakdown يظهر ديناميكياً إذا كانت هناك خطوات ومهمة ممتدة
                if (isRangeTask && task.milestones.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SectionHeading("Plan Breakdown")
                            Surface(color = Color(0xFFFDE7E7), shape = RoundedCornerShape(8.dp)) {
                                Text(
                                    text = "${task.milestones.size} Tasks",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    color = Color.Red,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // تحويل الـ List<String> الحقيقية الخاصة بالـ Milestones إلى الـ UI Items
                    itemsIndexed(task.milestones) { index, milestone ->
                        // محاكاة الحالة بناءً على مكان الخطوة كمثال للمظهر الجمالي الثابت عندك
                        val status = when {
                            index < task.milestones.size / 2 -> StepStatus.COMPLETED
                            index == task.milestones.size / 2 -> StepStatus.IN_PROGRESS
                            else -> StepStatus.UPCOMING
                        }
                        val subtext = when (status) {
                            StepStatus.COMPLETED -> "Completed"
                            StepStatus.IN_PROGRESS -> "In Progress"
                            StepStatus.UPCOMING -> "Upcoming"
                        }
                        PlanStepItem(PlanStep("Day ${index + 1}: $milestone", subtext, status))
                    }
                }

                if (task.reminderEnabled) {
                    item { ReminderCard("5 mins before focus hours") }
                }
            }
        }
    }
}

@Composable
fun StatsGrid(dateValue: String, priority: String, dailyFocus: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFF7F9FC)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                StatItem(if (dateValue.contains("-")) "DATE RANGE" else "START DATE", dateValue, Icons.Default.CalendarToday, Modifier.weight(1f))
                StatItem("FOCUS LEVEL", "$priority Priority", Icons.Default.Speed, Modifier.weight(1f))
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                StatItem("DAILY FOCUS", dailyFocus, Icons.Default.AccessTime, Modifier.weight(1f))
                if (dateValue.contains("-")) {
                    StatItem("COMPLETION", "65%", Icons.Default.DonutLarge, Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, fontSize = 11.sp, color = NavyPrimary.copy(alpha = 0.5f), fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (label == "DAILY FOCUS" || label == "COMPLETION") {
                Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp), tint = NavyPrimary)
                Spacer(Modifier.width(6.dp))
            }
            Text(value, fontSize = 16.sp, color = NavyPrimary, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
fun ReminderCard(time: String) {
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
                modifier = Modifier.size(44.dp).background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Notifications, null, tint = NavyPrimary, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text("Reminder: Enabled", fontWeight = FontWeight.ExtraBold, color = NavyPrimary, fontSize = 16.sp)
                Text(text = time, fontSize = 13.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun DeadlineProgress(progress: Float, daysLeft: Int) {
    Column(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Progress to deadline", fontSize = 13.sp, color = NavyPrimary, fontWeight = FontWeight.Bold)
            Text("$daysLeft Days left", fontSize = 13.sp, color = NavyPrimary, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(10.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth().height(10.dp),
            color = NavyPrimary,
            trackColor = Color(0xFFF0F0F0),
            strokeCap = StrokeCap.Round
        )
    }
}

@Composable
fun PlanStepItem(step: PlanStep) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = if (step.status == StepStatus.IN_PROGRESS) Color.White else Color(0xFFF7F9FC),
        border = if (step.status == StepStatus.IN_PROGRESS) BorderStroke(2.dp, NavyPrimary) else null
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(if (step.status == StepStatus.COMPLETED) NavyPrimary else Color.White, CircleShape)
                    .border(1.5.dp, if (step.status == StepStatus.UPCOMING) Color.LightGray else NavyPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (step.status == StepStatus.COMPLETED) Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(18.dp))
                if (step.status == StepStatus.IN_PROGRESS) Box(Modifier.size(10.dp).background(NavyPrimary, CircleShape))
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(step.title, fontWeight = FontWeight.Bold, color = if (step.status == StepStatus.UPCOMING) Color.Gray else NavyPrimary, fontSize = 15.sp)
                Text(step.subtext, fontSize = 12.sp, color = NavyPrimary.copy(alpha = 0.5f))
            }
            if (step.status == StepStatus.IN_PROGRESS) Icon(Icons.Default.PlayCircleFilled, null, tint = NavyPrimary, modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
fun SectionHeading(text: String) {
    Text(text, fontSize = 22.sp, color = NavyPrimary, fontWeight = FontWeight.Black)
}

data class PlanStep(val title: String, val subtext: String, val status: StepStatus)
enum class StepStatus { COMPLETED, IN_PROGRESS, UPCOMING }