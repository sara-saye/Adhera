package com.gpproject.adhera.treatment.todo_list.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.gpproject.adhera.ui.theme.*

@Composable
fun EditTaskScreen(
    taskId: String,
    onBack: () -> Unit,
    onSettings: () -> Unit = {},
    viewModel: TaskViewModel
) {
    // نحمل التاسك من الـ ViewModel
    LaunchedEffect(taskId) {
        viewModel.loadTaskById(taskId)
    }

    val currentTask by viewModel.currentTaskState.collectAsStateWithLifecycle()

    // نملأ الـ state بالبيانات الحقيقية لما التاسك يتحمل
    var title           by remember { mutableStateOf("") }
    var description     by remember { mutableStateOf("") }
    var startTime       by remember { mutableStateOf("09:00 AM") }
    var endDate         by remember { mutableStateOf("") }
    var dailyFocus      by remember { mutableStateOf("02:30 HRS") }
    var reminderEnabled by remember { mutableStateOf(true) }
    var milestones      by remember { mutableStateOf<List<String>>(emptyList()) }
    var newMilestone    by remember { mutableStateOf("") }
    var showAddField    by remember { mutableStateOf(false) }

    // لما التاسك يتحمل نملأ الحقول
    LaunchedEffect(currentTask) {
        currentTask?.let { task ->
            title           = task.title
            description     = task.description
            startTime       = task.startTime
            endDate         = task.endDate ?: ""
            dailyFocus      = task.dailyFocus
            reminderEnabled = task.reminderEnabled
            milestones      = task.milestones
        }
    }

    // لو التاسك لسه بتتحمل
    if (currentTask == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = NavyPrimary)
        }
        return
    }

    val task = currentTask!!
    val hasSubTasks = milestones.isNotEmpty()

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
                    text = "Edit Task",
                    fontSize = 20.sp,
                    color = NavyPrimary,
                    fontWeight = FontWeight.ExtraBold
                )
                IconButton(onClick = onSettings) {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = null,
                        tint = NavyPrimary,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        },
        containerColor = Color(0xFFF8F9FB)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // ─── كارت العنوان ─────────────────────────────────────────────────
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color.White
            ) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(50.dp),
                        color = Color(0xFFEFF3F8),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = NavyPrimary,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary,
                            fontSize = 18.sp
                        )
                        Text(
                            text = task.durationType,
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // ─── وقت وتاريخ ──────────────────────────────────────────────────
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                EditInfoField(
                    label = "START TIME",
                    value = startTime,
                    icon = Icons.Default.AccessTime,
                    modifier = Modifier.weight(1f)
                )
                if (task.endDate != null) {
                    EditInfoField(
                        label = "END DATE",
                        value = endDate.ifEmpty { task.startDate },
                        icon = Icons.Default.CalendarToday,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            EditInfoField(
                label = "FOCUS DURATION",
                value = dailyFocus,
                icon = Icons.Default.Timer,
                modifier = Modifier.fillMaxWidth()
            )

            // ─── الوصف ───────────────────────────────────────────────────────
            Column {
                Text(
                    "DESCRIPTION",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
                Spacer(Modifier.height(8.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = description.ifEmpty { "No description added." },
                            fontSize = 14.sp,
                            color = NavyPrimary.copy(alpha = 0.8f),
                            lineHeight = 22.sp
                        )
                        if (hasSubTasks) {
                            Spacer(Modifier.height(12.dp))
                            // ─── زرار Re-generate مربوط بالـ AI ───
                            val isGenerating by viewModel.isGeneratingMilestones.collectAsStateWithLifecycle()
                            Button(
                                onClick = {
                                    viewModel.generateAiMilestones(
                                        title = title,
                                        description = description
                                    ) { newSteps ->
                                        milestones = newSteps
                                    }
                                },
                                enabled = !isGenerating,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFF1F4F9)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                if (isGenerating) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = NavyPrimary,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        Icons.Default.AutoAwesome,
                                        null,
                                        tint = NavyPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "Re-generate with AI",
                                        color = NavyPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // ─── Reminder ────────────────────────────────────────────────────
            Column {
                Text("REMINDER", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(Modifier.height(8.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.White
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.NotificationsNone, null, tint = NavyPrimary)
                        Spacer(Modifier.width(12.dp))
                        Text(
                            "5-minute before alert",
                            modifier = Modifier.weight(1f),
                            fontSize = 14.sp,
                            color = NavyPrimary
                        )
                        Switch(
                            checked = reminderEnabled,
                            onCheckedChange = { reminderEnabled = it }
                        )
                    }
                }
            }

            // ─── Sub-tasks / Milestones ───────────────────────────────────────
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "SUB-TASKS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                    TextButton(onClick = { showAddField = !showAddField }) {
                        Icon(
                            Icons.Default.AddCircleOutline,
                            null,
                            modifier = Modifier.size(16.dp),
                            tint = NavyPrimary
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "Add New Step",
                            color = NavyPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // حقل إضافة milestone جديد
                if (showAddField) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newMilestone,
                            onValueChange = { newMilestone = it },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("New step...") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        IconButton(onClick = {
                            if (newMilestone.isNotBlank()) {
                                milestones = milestones + newMilestone.trim()
                                newMilestone = ""
                                showAddField = false
                            }
                        }) {
                            Icon(Icons.Default.Check, null, tint = NavyPrimary)
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                }

                // قائمة الـ milestones
                milestones.forEachIndexed { index, step ->
                    EditSubTaskItem(
                        number = index + 1,
                        text = step,
                        onDelete = {
                            milestones = milestones.toMutableList().also { it.removeAt(index) }
                        }
                    )
                    Spacer(Modifier.height(10.dp))
                }

                // لو مافيش milestones
                if (milestones.isEmpty()) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = Color(0xFFF7F9FC)
                    ) {
                        Text(
                            text = "No sub-tasks yet. Tap 'Add New Step' or use AI to generate.",
                            modifier = Modifier.padding(16.dp),
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            // ─── Save Button ──────────────────────────────────────────────────
            Button(
                onClick = {
                    viewModel.upsertTask(
                        task.copy(
                            title           = title,
                            description     = description,
                            startTime       = startTime,
                            endDate         = endDate.ifEmpty { task.endDate },
                            dailyFocus      = dailyFocus,
                            reminderEnabled = reminderEnabled,
                            milestones      = milestones
                        )
                    ) { onBack() }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text("Save Changes", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            // ─── Delete Button ────────────────────────────────────────────────
            TextButton(
                onClick = {
                    viewModel.deleteTask(task) { onBack() }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Delete Task", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ─── Helper Composables ───────────────────────────────────────────────────────

@Composable
fun EditInfoField(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Spacer(Modifier.height(8.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = Color.White
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                Icon(icon, null, modifier = Modifier.size(20.dp), tint = Color.LightGray)
            }
        }
    }
}

@Composable
fun EditSubTaskItem(number: Int, text: String, onDelete: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color.White
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(NavyPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("$number", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Text(text, modifier = Modifier.weight(1f), fontSize = 14.sp, color = NavyPrimary)
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Default.DeleteOutline,
                    null,
                    tint = Color(0xFFE57373),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}