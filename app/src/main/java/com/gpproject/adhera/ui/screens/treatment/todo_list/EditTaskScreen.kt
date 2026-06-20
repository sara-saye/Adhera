package com.gpproject.adhera.ui.screens.treatment.todo_list

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
import androidx.compose.ui.tooling.preview.Preview
import com.gpproject.adhera.ui.theme.*
import com.gpproject.adhera.viewmodels.TaskViewModel

@Composable
fun EditTaskScreen(
    taskId: String = "1",
    taskTitleDefault: String = "Hello Deep Work",
    hasSubTasks: Boolean = true, // باراميتر للتحكم في ظهور الـ AI والـ Sub-tasks
    onBack: () -> Unit,
    onSettings: () -> Unit = {},
    viewModel: TaskViewModel
) {
    var title by remember { mutableStateOf(taskTitleDefault) }
    var description by remember { mutableStateOf("Focusing on the documentation for the new Adhera UI kit, ensuring all components follow accessibility guidelines...") }
    var reminderEnabled by remember { mutableStateOf(true) }

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
                    text = "Edit Task",
                    fontSize = 20.sp,
                    color = NavyPrimary,
                    fontWeight = FontWeight.ExtraBold
                )
                IconButton(onClick = onSettings) {
                    Icon(Icons.Default.Settings, contentDescription = null, tint = NavyPrimary, modifier = Modifier.size(26.dp))
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
            // كارت العنوان
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = Color.White
            ) {
                Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(modifier = Modifier.size(50.dp), color = Color(0xFFEFF3F8), shape = RoundedCornerShape(12.dp)) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = NavyPrimary, modifier = Modifier.padding(12.dp))
                    }
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(text = title, fontWeight = FontWeight.Bold, color = NavyPrimary, fontSize = 18.sp)
                        Text(text = "Pre-filled from your schedule", color = Color.Gray, fontSize = 13.sp)
                    }
                }
            }

            // خانات الوقت والتاريخ
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                EditFieldItem(label = "START TIME", value = "09:00 AM", icon = Icons.Default.AccessTime, modifier = Modifier.weight(1f))
                EditFieldItem(label = "END DATE", value = "Oct 24, 2023", icon = Icons.Default.CalendarToday, modifier = Modifier.weight(1f))
            }

            EditFieldItem(label = "FOCUS DURATION", value = "2.5 Hours", icon = Icons.Default.Timer, modifier = Modifier.fillMaxWidth())

            // قسم الوصف - زرار الـ AI يظهر بشرط
            Column {
                Text("DESCRIPTION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(Modifier.height(8.dp))
                Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = Color.White) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = description, fontSize = 14.sp, color = NavyPrimary.copy(alpha = 0.8f), lineHeight = 22.sp)

                        if (hasSubTasks) { // إخفاء الزرار لو التاسك بسيطة
                            Spacer(Modifier.height(12.dp))
                            Button(
                                onClick = {},
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F4F9)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.AutoAwesome, null, tint = NavyPrimary, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Re-generate with AI", color = NavyPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // المنبه
            Column {
                Text("REMINDER", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                Spacer(Modifier.height(8.dp))
                Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = Color.White) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.NotificationsNone, null, tint = NavyPrimary)
                        Spacer(Modifier.width(12.dp))
                        Text("5-minute before alert", modifier = Modifier.weight(1f), fontSize = 14.sp, color = NavyPrimary)
                        Switch(checked = reminderEnabled, onCheckedChange = { reminderEnabled = it })
                    }
                }
            }

            // قسم المهام الفرعية يظهر بشرط
            if (hasSubTasks) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("SUB-TASKS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        TextButton(onClick = {}) {
                            Icon(Icons.Default.AddCircleOutline, null, modifier = Modifier.size(16.dp), tint = NavyPrimary)
                            Spacer(Modifier.width(4.dp))
                            Text("Add New Step", color = NavyPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    listOf("Review existing UI audit logs", "Draft typography hierarchy").forEachIndexed { index, sub ->
                        SubTaskItem(index + 1, sub)
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }

            // أزرار الحفظ والمسح
            Button(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text("Save Changes", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            TextButton(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                Text("Delete Task", color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun EditFieldItem(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Spacer(Modifier.height(8.dp))
        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = Color.White) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                Text(value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                Icon(icon, null, modifier = Modifier.size(20.dp), tint = Color.LightGray)
            }
        }
    }
}

@Composable
fun SubTaskItem(number: Int, text: String) {
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = Color.White) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(28.dp).background(NavyPrimary, CircleShape), contentAlignment = Alignment.Center) {
                Text("$number", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.width(12.dp))
            Text(text, modifier = Modifier.weight(1f), fontSize = 14.sp, color = NavyPrimary)
            Icon(Icons.Default.DeleteOutline, null, tint = Color(0xFFE57373), modifier = Modifier.size(20.dp))
        }
    }
}

// --- Previews ---

//@Preview(name = "Edit Task - With AI & Subtasks", showBackground = true, device = "spec:width=360dp,height=1200dp")
//@Composable
//fun EditTaskPreviewWithSteps() {
//    AdheraTheme {
//        EditTaskScreen(onBack = {},)
//    }
//}
//
//@Preview(name = "Edit Task - Simple (No AI/Steps)", showBackground = true, device = "spec:width=360dp,height=900dp")
//@Composable
//fun EditTaskPreviewSimple() {
//    AdheraTheme {
//        // تمرير false لإخفاء زر الـ AI والـ Sub-tasks
//        EditTaskScreen(taskTitleDefault = "Morning Deep Work", hasSubTasks = false, onBack = {},)
//    }
//}