package com.gpproject.adhera.ui.screens.treatment.todo_list

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.gpproject.adhera.ui.theme.*
import com.gpproject.adhera.viewmodels.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskScreen(
    onBack: () -> Unit,
    onSettings: () -> Unit = {},
    initialTab: String = "Today",
    forceDatePicker: Boolean = false,
    forceTimePicker: Boolean = false,
    viewModel: TaskViewModel

) {
    var taskTitle by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedDuration by remember { mutableStateOf(initialTab) }
    var showDatePicker by remember { mutableStateOf(forceDatePicker) }
    var showTimePicker by remember { mutableStateOf(forceTimePicker) }
    var reminderEnabled by remember { mutableStateOf(true) }
    var selectedPriority by remember { mutableStateOf("Medium") }

    val charCount = description.length
    val milestoneCount = when(selectedDuration) {
        "Week" -> 7
        "Month" -> 30
        "Custom" -> 5
        else -> 0
    }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier.fillMaxWidth().statusBarsPadding().padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBackIosNew, null, tint = NavyPrimary, modifier = Modifier.size(20.dp))
                }
                Text("Create Task", fontSize = 18.sp, color = NavyPrimary, fontWeight = FontWeight.Black)
                IconButton(onClick = onSettings) {
                    Icon(Icons.Default.Settings, null, tint = NavyPrimary, modifier = Modifier.size(24.dp))
                }
            }
        },
        containerColor = Color(0xFFF8F9FB)
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Duration Tabs
                Column {
                    Text("DURATION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth().background(Color(0xFFEEF1F6), RoundedCornerShape(12.dp)).padding(4.dp)) {
                        listOf("Today", "Week", "Month", "Custom").forEach { tab ->
                            val isSelected = selectedDuration == tab
                            Box(
                                modifier = Modifier.weight(1f).background(if (isSelected) NavyPrimary else Color.Transparent, RoundedCornerShape(8.dp))
                                    .clickable { selectedDuration = tab }.padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(tab, color = if (isSelected) Color.White else Color.Gray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // 2. Task Title
                CreateFieldItem(label = "TASK TITLE", value = taskTitle, onValueChange = { taskTitle = it }, placeholder = "enter your task..")

                // 3. Start Date Selection
                if (selectedDuration == "Custom") {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        EditFieldItem(label = "START DATE", value = "Oct 12", icon = Icons.Default.CalendarToday, modifier = Modifier.weight(1f)) { showDatePicker = true }
                        EditFieldItem(label = "END DATE", value = "Oct 18", icon = Icons.Default.CalendarToday, modifier = Modifier.weight(1f)) { showDatePicker = true }
                    }
                } else {
                    EditFieldItem(label = "START DATE", value = "September 04, 2024", icon = Icons.Default.CalendarMonth, modifier = Modifier.fillMaxWidth()) { showDatePicker = true }
                }

                // 4. Time & Focus
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    EditFieldItem(label = "START TIME", value = "09:00 AM", icon = Icons.Default.AccessTime, modifier = Modifier.weight(1f)) { showTimePicker = true }
                    EditFieldItem(label = "DAILY FOCUS", value = "02:30 HRS", icon = Icons.Default.Timer, modifier = Modifier.weight(1f)) { }
                }

                // 5. Priority
                Column {
                    Text("PRIORITY", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(Modifier.height(8.dp))
                    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = Color.White, border = BorderStroke(1.dp, Color(0xFFEEEEEE))) {
                        Row(modifier = Modifier.padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(Modifier.size(8.dp).background(Color(0xFFFFD600), CircleShape))
                                Spacer(Modifier.width(10.dp))
                                Text(selectedPriority, color = NavyPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Icon(Icons.Default.KeyboardArrowDown, null, tint = NavyPrimary)
                        }
                    }
                }

                // 6. Description
                Column {
                    Text("DESCRIPTION", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Spacer(Modifier.height(8.dp))
                    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = Color.White, border = BorderStroke(1.dp, Color(0xFFEEEEEE))) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            BasicTextField(
                                value = description,
                                onValueChange = { description = it },
                                modifier = Modifier.fillMaxWidth().height(80.dp),
                                textStyle = TextStyle(color = NavyPrimary, fontSize = 14.sp),
                                decorationBox = { innerTextField ->
                                    if (description.isEmpty()) Text("describe your task in details", color = Color.LightGray, fontSize = 14.sp)
                                    innerTextField()
                                }
                            )
                            if (selectedDuration != "Today") {
                                Text(
                                    text = "$charCount / 20 characters",
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.End,
                                    fontSize = 10.sp,
                                    color = if (charCount < 20) Color.Red else Color.Gray
                                )
                            }
                        }
                    }
                }

                // 7. AI Planning Box - Navy Background
                if (selectedDuration != "Today") {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        color = NavyPrimary
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Stuck on planning?", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text(
                                    "Let AI break your task into simple, manageable steps.",
                                    color = Color.White.copy(0.8f),
                                    fontSize = 11.sp,
                                    lineHeight = 14.sp
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            Button(
                                onClick = {},
                                enabled = charCount >= 20,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF0288D1), // Sky Blue أغمق شوية عشان الأبيض يبان
                                    contentColor = Color.White,        // النص أبيض
                                    disabledContainerColor = Color.White.copy(0.1f)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp)
                            ) {
                                Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(14.dp), tint = Color.White)
                                Spacer(Modifier.width(4.dp))
                                Text("Generate", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                        }
                    }

                    // 8. TASK MILESTONE
                    Column {
                        Text("TASK MILESTONE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                        Spacer(Modifier.height(8.dp))
                        repeat(milestoneCount) { index ->
                            Surface(Modifier.fillMaxWidth().padding(bottom = 8.dp), shape = RoundedCornerShape(12.dp), color = Color.White, border = BorderStroke(1.dp, Color(0xFFF0F0F0))) {
                                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Box(Modifier.size(24.dp).background(Color(0xFFEEF1F6), CircleShape), contentAlignment = Alignment.Center) {
                                        Text("${index + 1}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
                                    }
                                    Spacer(Modifier.width(12.dp))
                                    Text("add step", color = Color.LightGray, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }

                // 9. Reminder (Exactly as you wanted)
                Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), color = Color.White, border = BorderStroke(1.dp, Color(0xFFEEEEEE))) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Outlined.NotificationsActive, null, tint = NavyPrimary, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("5 minutes before", color = NavyPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("A gentle reminder to refocus.", color = Color.Gray, fontSize = 11.sp)
                        }
                        Switch(
                            checked = reminderEnabled,
                            onCheckedChange = { reminderEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = NavyPrimary,
                                uncheckedTrackColor = Color(0xFFE0E0E0),
                                uncheckedThumbColor = Color.White
                            )
                        )
                    }
                }

                // 10. Final Save Button
                Button(
                    onClick = {},
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Save Task", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                }
                Spacer(Modifier.height(24.dp))
            }

            // --- Overlays (Calendar & Time Wheels) ---
            if (showDatePicker) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.5f)).clickable { showDatePicker = false }, contentAlignment = Alignment.Center) {
                    Surface(modifier = Modifier.padding(24.dp).fillMaxWidth(), shape = RoundedCornerShape(28.dp), color = Color.White) {
                        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ChevronLeft, null, tint = NavyPrimary)
                                Text("September 2024", fontWeight = FontWeight.Black, color = NavyPrimary, fontSize = 16.sp)
                                Icon(Icons.Default.ChevronRight, null, tint = NavyPrimary)
                            }
                            Spacer(Modifier.height(16.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                                listOf("S","M","T","W","T","F","S").forEach { Text(it, color = Color.LightGray, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            }
                            Spacer(Modifier.height(8.dp))
                            Column {
                                repeat(5) { row ->
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                                        repeat(7) { col ->
                                            val dateIdx = row * 7 + col - 2
                                            if (dateIdx in 1..30) {
                                                val isSelected = dateIdx == 4
                                                Box(Modifier.size(32.dp).background(if(isSelected) NavyPrimary else Color.Transparent, CircleShape), contentAlignment = Alignment.Center) {
                                                    Text("$dateIdx", color = if(isSelected) Color.White else Color.DarkGray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                                }
                                            } else { Spacer(Modifier.size(32.dp)) }
                                        }
                                    }
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = { showDatePicker = false }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(NavyPrimary)) {
                                Text("Confirm")
                            }
                        }
                    }
                }
            }

            if (showTimePicker) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(0.5f)).clickable { showTimePicker = false }, contentAlignment = Alignment.Center) {
                    Surface(modifier = Modifier.width(260.dp), shape = RoundedCornerShape(28.dp), color = Color.White) {
                        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Start Time", fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 12.sp)
                            Spacer(Modifier.height(20.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("08", color = Color.LightGray, fontSize = 16.sp)
                                    Text("09", color = NavyPrimary, fontSize = 44.sp, fontWeight = FontWeight.Black)
                                    Text("10", color = Color.LightGray, fontSize = 16.sp)
                                }
                                Text(" : ", fontSize = 32.sp, color = NavyPrimary, fontWeight = FontWeight.Bold)
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("55", color = Color.LightGray, fontSize = 16.sp)
                                    Text("00", color = NavyPrimary, fontSize = 44.sp, fontWeight = FontWeight.Black)
                                    Text("05", color = Color.LightGray, fontSize = 16.sp)
                                }
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.background(NavyPrimary, RoundedCornerShape(10.dp)).padding(4.dp)) {
                                    Box(Modifier.background(Color.White, RoundedCornerShape(6.dp)).padding(horizontal = 6.dp, vertical = 2.dp)) {
                                        Text("AM", color = NavyPrimary, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                                    }
                                    Text("PM", color = Color.White.copy(0.5f), fontWeight = FontWeight.Bold, fontSize = 10.sp, modifier = Modifier.padding(4.dp))
                                }
                            }
                            Spacer(Modifier.height(24.dp))
                            Button(onClick = { showTimePicker = false }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(NavyPrimary)) {
                                Text("Set Time")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EditFieldItem(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(modifier = modifier) {
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Spacer(Modifier.height(6.dp))
        Surface(modifier = Modifier.fillMaxWidth().clickable { onClick() }, shape = RoundedCornerShape(12.dp), color = Color.White, border = BorderStroke(1.dp, Color(0xFFEEEEEE))) {
            Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = NavyPrimary, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(10.dp))
                Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NavyPrimary)
            }
        }
    }
}

@Composable
fun CreateFieldItem(label: String, value: String, onValueChange: (String) -> Unit, placeholder: String) {
    Column {
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Spacer(Modifier.height(6.dp))
        Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), color = Color.White, border = BorderStroke(1.dp, Color(0xFFEEEEEE))) {
            Box(modifier = Modifier.padding(14.dp)) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(color = NavyPrimary, fontSize = 14.sp),
                    decorationBox = { innerTextField ->
                        if (value.isEmpty()) Text(placeholder, color = Color.LightGray, fontSize = 14.sp)
                        innerTextField()
                    }
                )
            }
        }
    }
}
// --- Previews ---
//private const val PRE_DEV = "spec:width=360dp,height=1000dp,dpi=440"
//
//@Preview(name = "1. Today View", device = PRE_DEV, showBackground = true)
//@Composable fun Prev1() { AdheraTheme { CreateTaskScreen(onBack = {},) } }
//
//@Preview(name = "2. Week View", device = PRE_DEV, showBackground = true)
//@Composable fun Prev2() { AdheraTheme { CreateTaskScreen(onBack = {}, initialTab = "Week",) } }
//
//@Preview(name = "3. Custom Range", device = PRE_DEV, showBackground = true)
//@Composable fun Prev3() { AdheraTheme { CreateTaskScreen(onBack = {}, initialTab = "Custom",) } }
//
//@Preview(name = "4. Month View", device = PRE_DEV, showBackground = true)
//@Composable fun Prev4() { AdheraTheme { CreateTaskScreen(onBack = {}, initialTab = "Month",) } }
//
//@Preview(name = "5. Calendar Picker", device = PRE_DEV, showBackground = true)
//@Composable fun Prev5() { AdheraTheme { CreateTaskScreen(onBack = {}, forceDatePicker = true,) } }
//
//@Preview(name = "6. Time Picker", device = PRE_DEV, showBackground = true)
//@Composable fun Prev6() { AdheraTheme { CreateTaskScreen(onBack = {}, forceTimePicker = true,) } }