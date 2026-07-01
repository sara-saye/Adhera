package com.gpproject.adhera.treatment.todo_list.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gpproject.adhera.treatment.todo_list.tododb.TaskEntity
import com.gpproject.adhera.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoListScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (String) -> Unit,
    onNavigateToDetails: (String) -> Unit,
    onBack: () -> Unit,
    initialTab: Int = 0,
    viewModel: TaskViewModel
) {
    var selectedTab by remember { mutableIntStateOf(initialTab) }
    val allTasks by viewModel.tasksState.collectAsStateWithLifecycle()

    // فلترة التاسكات حسب الـ Tab
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
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.White,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = NavyPrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(28.dp))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 20.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.Default.ArrowBackIosNew,
                        contentDescription = null,
                        tint = NavyPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = "To Do",
                    fontSize = 22.sp,
                    color = NavyPrimary,
                    fontWeight = FontWeight.ExtraBold
                )
                IconButton(onClick = { }) {
                    Icon(Icons.Default.MoreVert, contentDescription = null, tint = NavyPrimary)
                }
            }

            Text(
                text = "Let's get things done\ntoday",
                fontSize = 28.sp,
                lineHeight = 34.sp,
                color = NavyPrimary,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            // Tabs
            val tabs = listOf("Today", "This week", "This month")
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = NavyPrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = NavyPrimary
                    )
                },
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 14.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == index) NavyPrimary else Color.Gray
                            )
                        }
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Your Tasks",
                    fontSize = 16.sp,
                    color = NavyPrimary,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { viewModel.clearCompletedTasks() }) {
                    Text(
                        text = "Clear Completed",
                        color = NavyPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                if (filteredTasks.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.CheckCircleOutline,
                                    contentDescription = null,
                                    tint = Color.LightGray,
                                    modifier = Modifier.size(60.dp)
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    "No tasks yet!\nTap + to add one.",
                                    color = Color.Gray,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    lineHeight = 22.sp
                                )
                            }
                        }
                    }
                } else {
                    items(
                        items = filteredTasks,
                        key = { it.id }
                    ) { task ->
                        SwipeableTaskItem(
                            task = task,
                            selectedTab = selectedTab,
                            onEdit = { onNavigateToEdit(task.id) },
                            onDelete = { viewModel.deleteTask(task) },
                            onToggleComplete = { viewModel.toggleTaskCompletion(task) },
                            onTap = { onNavigateToDetails(task.id) }
                        )
                    }
                }

                item {
                    FocusTipBox()
                }
            }
        }
    }
}

// ─── Swipeable Task Item ──────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeableTaskItem(
    task: TaskEntity,
    selectedTab: Int,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleComplete: () -> Unit,
    onTap: () -> Unit
) {
    // نحتفظ بـ state للـ dismiss
    var isDismissed by remember { mutableStateOf(false) }

    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                // سحب لليمين → حذف
                SwipeToDismissBoxValue.StartToEnd -> {
                    isDismissed = true
                    onDelete()
                    true
                }
                // سحب لليسار → تعديل
                SwipeToDismissBoxValue.EndToStart -> {
                    onEdit()
                    false // مش بنخبي العنصر، بس بنفتح شاشة التعديل
                }
                else -> false
            }
        },
        positionalThreshold = { it * 0.4f } // لازم يسحب 40% عشان يكمل
    )

    if (!isDismissed) {
        SwipeToDismissBox(
            state = dismissState,
            backgroundContent = {
                SwipeBackground(dismissState)
            },
            content = {
                TaskCard(
                    task = task,
                    selectedTab = selectedTab,
                    onToggleComplete = onToggleComplete,
                    onTap = onTap
                )
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeBackground(dismissState: SwipeToDismissBoxState) {
    val direction = dismissState.dismissDirection

    // لو سحب يمين = Delete (أحمر) | لو سحب شمال = Edit (أزرق)
    val isDelete = direction == SwipeToDismissBoxValue.StartToEnd
    val isEdit   = direction == SwipeToDismissBoxValue.EndToStart

    val color by animateColorAsState(
        targetValue = when {
            isDelete -> Color(0xFFE53935)
            isEdit   -> NavyPrimary
            else     -> Color.Transparent
        },
        label = "swipe_bg_color"
    )

    val scale by animateFloatAsState(
        targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.Settled) 0.75f else 1f,
        label = "swipe_icon_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color, RoundedCornerShape(18.dp))
            .padding(horizontal = 20.dp),
        contentAlignment = if (isEdit) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        if (isDelete) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Delete",
                tint = Color.White,
                modifier = Modifier.scale(scale).size(26.dp)
            )
        } else if (isEdit) {
            Icon(
                Icons.Default.Edit,
                contentDescription = "Edit",
                tint = Color.White,
                modifier = Modifier.scale(scale).size(26.dp)
            )
        }
    }
}

// ─── Task Card (المحتوى الفعلي للعنصر) ───────────────────────────────────────

@Composable
fun TaskCard(
    task: TaskEntity,
    selectedTab: Int,
    onToggleComplete: () -> Unit,
    onTap: () -> Unit
) {
    val priorityColor = when (task.priority) {
        "High"   -> Color(0xFFE57373)
        "Medium" -> Color(0xFFFFB74D)
        else     -> Color(0xFF81C784)
    }

    // تحديد النص اللي يظهر تحت العنوان حسب الـ Tab
    val subtitle = when (selectedTab) {
        0    -> task.startTime
        else -> if (task.endDate != null) "${task.startDate} - ${task.endDate}" else task.startDate
    }

    Surface(
        onClick = onTap,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFFF7F9FC),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Checkbox + Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Checkbox دائري
                Surface(
                    onClick = onToggleComplete,
                    modifier = Modifier.size(24.dp),
                    shape = CircleShape,
                    color = if (task.isCompleted) NavyPrimary else Color.White,
                    border = BorderStroke(1.5.dp, NavyPrimary)
                ) {
                    if (task.isCompleted) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .padding(4.dp)
                                .size(16.dp)
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = task.title,
                        fontWeight = FontWeight.Bold,
                        color = NavyPrimary,
                        fontSize = 16.sp,
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                    )
                    Text(text = subtitle, fontSize = 12.sp, color = Color.Gray)
                }
            }

            // Priority Badge
            Surface(
                color = priorityColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = task.priority,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    color = priorityColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

// ─── Focus Tip ───────────────────────────────────────────────────────────────

@Composable
fun FocusTipBox() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        colors = CardDefaults.cardColors(containerColor = NavyPrimary),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Focus Tip",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Text(
                text = "Eliminate visual clutter.",
                color = Color.Magenta,
                fontWeight = FontWeight.Black,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Focusing on one task at a time increases completion rate by 40%.",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TodoTodayPreview() {
    AdheraTheme {
        // Preview بدون ViewModel حقيقي - للـ UI فقط
    }
}