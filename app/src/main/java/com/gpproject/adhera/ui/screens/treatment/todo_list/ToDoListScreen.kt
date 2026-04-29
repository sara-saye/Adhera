package com.gpproject.adhera.ui.screens.treatment.todo_list

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gpproject.adhera.viewmodels.TaskViewModel // اتأكدي إن المسار ده صح حسب مشروعك
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBackIosNew, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(20.dp))
                }
                Text(text = "To Do", fontSize = 22.sp, color = NavyPrimary, fontWeight = FontWeight.ExtraBold)
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

            // العناصر الأساسية (Your Tasks & Clear Completed) - رجعتهم تاني
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Your Tasks", fontSize = 16.sp, color = NavyPrimary, fontWeight = FontWeight.Bold)
                TextButton(onClick = { }) {
                    Text(text = "Clear Completed", color = NavyPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Tasks List
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                item {
                    // تاسك عادية (الوقت بيظهر في Today والتاريخ في الباقي)
                    TaskItem(
                        title = "Project Research",
                        subtitle = if (selectedTab == 0) "09:00 AM - 11:30 AM" else "Oct 1 - Oct 15",
                        priority = "High",
                        isCompleted = false
                    )
                }
                item {
                    // تاسك فيها زرار الـ edit/delete
                    TaskItem(
                        title = "Team Sync",
                        subtitle = if (selectedTab == 0) "12:00 PM - 01:00 PM" else "Oct 5 - Oct 20",
                        priority = "Medium",
                        isCompleted = false,
                        showActions = true
                    )
                }
                item {
                    // تاسك منتهية (Checked)
                    TaskItem(
                        title = "Check Emails",
                        subtitle = if (selectedTab == 0) "08:00 AM - 08:30 AM" else "Oct 1 - Oct 31",
                        priority = "Low",
                        isCompleted = true
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
fun TaskItem(title: String, subtitle: String, priority: String, isCompleted: Boolean, showActions: Boolean = false) {
    val priorityColor = when (priority) {
        "High" -> Color(0xFFE57373)
        "Medium" -> Color(0xFFFFB74D)
        else -> Color(0xFF81C784)
    }

    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFFF7F9FC),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0))
        ) {
            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier.size(24.dp).background(if (isCompleted) NavyPrimary else Color.White, CircleShape).border(1.5.dp, NavyPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isCompleted) Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            color = NavyPrimary,
                            fontSize = 16.sp,
                            textDecoration = if (isCompleted) TextDecoration.LineThrough else null
                        )
                        Text(text = subtitle, fontSize = 12.sp, color = Color.Gray)
                    }
                }

                Surface(color = priorityColor.copy(alpha = 0.1f), shape = RoundedCornerShape(8.dp)) {
                    Text(text = priority, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = priorityColor, fontSize = 10.sp, fontWeight = FontWeight.Black)
                }
            }
        }

        if (showActions) {
            Column(modifier = Modifier.padding(start = 8.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(modifier = Modifier.size(32.dp).background(NavyPrimary, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Edit, null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
                Box(modifier = Modifier.size(32.dp).background(Color(0xFFE57373), CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Delete, null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Composable
fun FocusTipBox() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
        colors = CardDefaults.cardColors(containerColor = NavyPrimary),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(text = "Focus Tip", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Text(text = "Eliminate visual clutter.", color = Color.Magenta, fontWeight = FontWeight.Black, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Focusing on one task at a time increases completion rate by 40%.",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

// الـ Preview المجمع للسكرينتين (Today & This Week)
@Preview(showBackground = true)
@Composable
fun TodoTodayPreview() {
    AdheraTheme {
        TodoListScreen(
            onNavigateToCreate = {},
            onNavigateToEdit = {},    // ضيفي السطر ده
            onNavigateToDetails = {}, // ضيفي السطر ده
            onBack = {}
        )
    }
}
// كرري نفس الإضافة (onNavigateToEdit و onNavigateToDetails) في باقي الـ Previews اللي تحت