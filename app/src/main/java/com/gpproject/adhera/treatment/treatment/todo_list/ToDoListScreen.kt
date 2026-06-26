package com.gpproject.adhera.treatment.treatment.todo_list

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gpproject.adhera.treatment.treatment.todo_list.tododb.TaskEntity // الموديل الحقيقي للداتابيز
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.gpproject.adhera.ui.theme.*

@Composable
fun TodoListScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (String) -> Unit,    // ضيفي السطر ده
    onNavigateToDetails: (String) -> Unit, // ضيفي السطر ده عشان سكرينة التفاصيل
    onBack: () -> Unit,
    initialTab: Int = 0,
    viewModel: TaskViewModel = viewModel()
) {
    var selectedTab by remember { mutableIntStateOf(initialTab) }

    // مراقبة لستة التاسكات الحقيقية القادمة من الـ Database
    val allTasks by viewModel.tasksState.collectAsState()

    // فلترة التاسكات حركياً بناءً على الـ Tab المفتوح
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
            // Header (Back - Adhera - More)
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

            // Main Title
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

            // العناصر الأساسية (Your Tasks & Clear Completed)
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
                TextButton(onClick = {
                    // مسح جميع التاسكات المنتهية من الـ database بضغطة واحدة
                    // viewModel.clearCompletedTasks()
                }) {
                    Text(
                        text = "Clear Completed",
                        color = NavyPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Tasks List
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                // عرض لستة البيانات الحقيقية ديناميكياً
                items(filteredTasks) { task ->
                    TaskItem(
                        task = task,
                        selectedTab = selectedTab,
                        // عرض أزرار التحكم لو التاسك مش منتهية كمثال أو حابة تثبتيها true براحتك
                        showActions = !task.isCompleted,
                        onToggleClick = { viewModel.toggleTaskCompletion(task) },
                        onItemClick = { onNavigateToDetails(task.id) },
                        onEditClick = { onNavigateToEdit(task.id) },
                        onDeleteClick = { viewModel.deleteTask(task) }
                    )
                }

                item {
                    FocusTipBox()
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    task: TaskEntity,
    selectedTab: Int,
    showActions: Boolean = false,
    onToggleClick: () -> Unit,
    onItemClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val priorityColor = when (task.priority) {
        "High" -> Color(0xFFE57373)
        "Medium" -> Color(0xFFFFB74D)
        else -> Color(0xFF81C784)
    }

    // تجهيز الـ subtitle بناءً على الـ Tab المفتوح والبيانات الحقيقية للتاسك
    val subtitleText = if (selectedTab == 0) {
        task.startTime
    } else {
        if (task.endDate != null) "${task.startDate} - ${task.endDate}" else task.startDate
    }

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier
                .weight(1f)
                .clickable { onItemClick() }, // الضغط على الكارد ينقل للتفاصيل
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFFF7F9FC),
            border = BorderStroke(1.dp, Color(0xFFE0E0E0))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // الـ Custom Checkbox مربوط بـ الـ click والـ state الحقيقية
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                if (task.isCompleted) NavyPrimary else Color.White,
                                CircleShape
                            )
                            .border(1.5.dp, NavyPrimary, CircleShape)
                            .clickable { onToggleClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (task.isCompleted) Icon(
                            Icons.Default.Check,
                            null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
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
                        Text(text = subtitleText, fontSize = 12.sp, color = Color.Gray)
                    }
                }

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

        // أزرار الـ Edit والـ Delete الحقيقية
        if (showActions) {
            Column(
                modifier = Modifier.padding(start = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(NavyPrimary, CircleShape)
                        .clickable { onEditClick() }, // ينقل لشاشة الـ Edit
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Edit,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color(0xFFE57373), CircleShape)
                        .clickable { onDeleteClick() }, // يمسح من الداتابيز
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Delete,
                        null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

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
        TodoListScreen(
            onNavigateToCreate = {},
            onNavigateToEdit = {},
            onNavigateToDetails = {},
            onBack = {}
        )
    }
}