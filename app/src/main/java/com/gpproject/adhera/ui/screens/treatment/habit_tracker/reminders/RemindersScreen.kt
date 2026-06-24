package com.gpproject.adhera.ui.screens.treatment.habit_tracker.reminders

import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale // ✅ تم إضافة الـ Import الصحيح هنا
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gpproject.adhera.data.local.habit.HabitServiceLocator
import com.gpproject.adhera.ui.screens.treatment.habit_tracker.AdheraViewModelFactory
//import com.gpproject.adhera.ui.common.AdheraViewModelFactory
import java.util.*

// الألوان المتوافقة مع الهوية البصرية لـ Bloom
val BloomDarkBlue = Color(0xFF032B43)
val BloomLightBlue = Color(0xFFBBE1FA)
val BloomActiveBlue = Color(0xFF3282B8)
val BloomBackground = Color(0xFFF7F9FC)
val BloomCardBg = Color(0xFFFFFFFF)
val BloomGrayText = Color(0xFF7F8C8D)
val BloomDarkCardBg = Color(0xFF032B43)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // ✅ بدل ما الشاشة تبني الداتابيز والريبوزيتوري بنفسها (وده كان بيعمل instance جديد
    // كل مرة الشاشة تتفتح)، بقينا نسحب نفس الـ Repository دايمًا من الـ ServiceLocator.
    // ده مش محتاج أي تسجيل في AndroidManifest لأن الفيتشر ده جوه أبلكيشن أكبر.
    val repository = remember { HabitServiceLocator.getRepository(context) }

    val viewModel: RemindersViewModel = viewModel(
        factory = AdheraViewModelFactory(repository)
    )

    val activeCount by viewModel.activeHabitsCount.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bloom", color = BloomDarkBlue, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Menu, contentDescription = null, tint = BloomDarkBlue)
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Search, contentDescription = null, tint = BloomDarkBlue)
                    }
                    Box(modifier = Modifier.padding(end = 12.dp).size(36.dp).clip(CircleShape).background(BloomLightBlue))
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BloomBackground)
            )
        },
        containerColor = BloomBackground,
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // 1. عنوان الشاشة العلوي وعدد العادات النشطة
            item {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(
                        text = "Reminders",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = BloomDarkBlue
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Manage your daily focus and notification\nschedule.",
                        fontSize = 14.sp,
                        color = BloomGrayText
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .background(BloomLightBlue.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text("$activeCount Active Habits", color = BloomDarkBlue, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            // 2. كارد تنبيهات شرب الماء (Hydration Target)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BloomCardBg)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(BloomDarkBlue),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("💧", fontSize = 16.sp)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Hydration Target", fontWeight = FontWeight.Bold, color = BloomDarkBlue)
                                    Text("Remind every 2 hours", fontSize = 11.sp, color = BloomGrayText)
                                }
                            }
                            Switch(
                                checked = viewModel.isHydrationEnabled,
                                onCheckedChange = { viewModel.toggleHydration(it) },
                                colors = SwitchDefaults.colors(checkedTrackColor = BloomDarkBlue)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                viewModel.hydrationSlots.take(2).forEach { time ->
                                    TimeSlotItem(time = time, modifier = Modifier.weight(1f))
                                }
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (viewModel.hydrationSlots.size > 2) {
                                    TimeSlotItem(time = viewModel.hydrationSlots[2], modifier = Modifier.weight(1f))
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(38.dp)
                                        .border(1.dp, BloomActiveBlue.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                        .clickable {
                                            val calendar = Calendar.getInstance()
                                            TimePickerDialog(context, { _, hour, minute ->
                                                val format = if (hour >= 12) "PM" else "AM"
                                                val displayHour = if (hour % 12 == 0) 12 else hour % 12
                                                viewModel.addHydrationSlot(String.format(Locale.US, "%02d:%02d %s", displayHour, minute, format))
                                            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false).show()
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Add, contentDescription = null, tint = BloomActiveBlue, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Add slot", color = BloomActiveBlue, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. كارد الدقائق الواعية الداكن (Mindful Minutes)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BloomDarkCardBg)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🧘", fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Mindful Minutes", fontWeight = FontWeight.Bold, color = Color.White)
                                    Text("Brief meditation prompts throughout your\nworkspace session.", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                                }
                            }
                            Switch(
                                checked = viewModel.isMindfulMinutesEnabled,
                                onCheckedChange = { viewModel.isMindfulMinutesEnabled = it },
                                colors = SwitchDefaults.colors(checkedTrackColor = BloomActiveBlue, uncheckedTrackColor = Color.White.copy(alpha = 0.3f))
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⏱️", fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("10:30 AM", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }

            // 4. كارد وقت العمل العميق (Deep Work Block)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BloomCardBg)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("💻", fontSize = 20.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("DEEP WORK BLOCK", fontWeight = FontWeight.Bold, color = BloomDarkBlue, fontSize = 12.sp)
                            }
                            Switch(
                                checked = viewModel.isDeepWorkEnabled,
                                onCheckedChange = { viewModel.isDeepWorkEnabled = it }
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Daily Schedule", color = BloomDarkBlue, fontSize = 14.sp)
                            Text("08:00 AM", fontWeight = FontWeight.Bold, color = BloomDarkBlue, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Divider(color = BloomGrayText.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("🔄 Mon, Tue, Wed, Thu, Fri", color = BloomGrayText, fontSize = 11.sp)
                    }
                }
            }

            // 5. كارد قراءة الكتب (Read 10 Pages) الموضح به الـ Streak وطريقة الـ Scale الصحيحة
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BloomCardBg)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("📚", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Read 10 Pages", fontWeight = FontWeight.Bold, color = BloomDarkBlue)
                                Text("Consistency is key to knowledge growth.", fontSize = 11.sp, color = BloomGrayText)
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Time", fontSize = 11.sp, color = BloomGrayText)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("09:30 PM", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = BloomDarkBlue)
                            IconButton(onClick = {}, modifier = Modifier.background(BloomBackground, CircleShape).size(32.dp)) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = BloomDarkBlue)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(BloomBackground, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Notification Active", fontSize = 12.sp, color = BloomDarkBlue, fontWeight = FontWeight.SemiBold)
                                    Switch(
                                        checked = viewModel.isReadingNotificationEnabled,
                                        onCheckedChange = { viewModel.isReadingNotificationEnabled = it },
                                        modifier = Modifier.scale(0.8f) // ✅ تم التعديل لتعمل بشكل صحيح الآن
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                LinearProgressIndicator(
                                    progress = { 0.75f },
                                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                                    color = BloomDarkBlue,
                                    trackColor = BloomLightBlue
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("22 days streak — keep going!", fontSize = 11.sp, color = BloomGrayText)
                            }
                        }
                    }
                }
            }

            // 6. جزء كارد الملخص اليومي (Daily Summary) أسفل الشاشة
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BloomDarkCardBg)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(36.dp).clip(RoundedCornerShape(8.dp)).background(Color.White.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("📋", fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Daily Summary", fontWeight = FontWeight.Bold, color = Color.White)
                                Text("A holistic recap of your productivity and habit trends.", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f))
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White.copy(alpha = 0.1f))
                                .padding(4.dp)
                        ) {
                            val isMorning = viewModel.isMorningSummary
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isMorning) Color.White else Color.Transparent)
                                    .clickable { viewModel.isMorningSummary = true }
                                    .padding(horizontal = 16.dp, vertical = 6.dp)
                            ) {
                                Text("Morning", color = if (isMorning) BloomDarkBlue else Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (!isMorning) Color.White else Color.Transparent)
                                    .clickable { viewModel.isMorningSummary = false }
                                    .padding(horizontal = 16.dp, vertical = 6.dp)
                            ) {
                                Text("Evening", color = if (!isMorning) BloomDarkBlue else Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("SCHEDULED TIME", fontSize = 10.sp, color = BloomGrayText, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(viewModel.summaryTime, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = BloomDarkBlue)
                                    Text("AM", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = BloomDarkBlue)
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                Divider(color = BloomGrayText.copy(alpha = 0.1f))
                                Spacer(modifier = Modifier.height(12.dp))

                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(Icons.Default.Info, contentDescription = null, tint = BloomActiveBlue, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Your Morning Recap will include a weather forecast, top 3 priorities for the day, and a motivational quote based on your recent 22-day reading streak.",
                                        fontSize = 11.sp,
                                        color = BloomDarkBlue,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TimeSlotItem(time: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(38.dp)
            .background(BloomBackground, RoundedCornerShape(6.dp)),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("⏰", fontSize = 12.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Text(time, fontSize = 12.sp, color = BloomDarkBlue, fontWeight = FontWeight.Medium)
        }
    }
}