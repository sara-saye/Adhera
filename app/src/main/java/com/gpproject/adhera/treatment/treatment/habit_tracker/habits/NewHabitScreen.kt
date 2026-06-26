package com.gpproject.adhera.treatment.treatment.habit_tracker.habits
import android.app.TimePickerDialog
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gpproject.adhera.treatment.treatment.habit_tracker.habitdb.HabitServiceLocator
import com.gpproject.adhera.ui.screens.treatment.habit_tracker.AdheraViewModelFactory
//import com.gpproject.adhera.ui.common.AdheraViewModelFactory
import java.util.*

// الألوان الخاصة بالتصميم
val BloomDarkBlue = Color(0xFF032B43)
val BloomLightBlue = Color(0xFFBBE1FA)
val BloomBackground = Color(0xFFF7F9FC)
val BloomCardBg = Color(0xFFFFFFFF)
val BloomGrayText = Color(0xFF7F8C8D)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewHabitScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // ✅ استخدام الـ Repository من ServiceLocator (بدون أي تسجيل في Manifest)
    val repository = remember { HabitServiceLocator.getRepository(context) }

    val viewModel: NewHabitViewModel = viewModel(
        factory = AdheraViewModelFactory(repository)
    )

    // قوائم الخيارات الثابتة الموجودة في الصورة image_a96acb.png
    val categories = listOf("Mind", "Body", "Focus", "Finance", "Social")
    val colors = listOf("#032B43", "#3282B8", "#8B4513", "#A00000", "#4A2E00")
    val daysOfWeek = listOf("M", "T", "W", "T", "F", "S", "S")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bloom", color = BloomDarkBlue, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع", tint = BloomDarkBlue)
                    }
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // الكارد الرئيسي المحتوي على حقول الإدخال
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BloomCardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("New Habit", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = BloomDarkBlue)

                        // 1. حقل إدخال الاسم
                        Text("Habit Name", fontSize = 12.sp, color = BloomDarkBlue, fontWeight = FontWeight.SemiBold)
                        OutlinedTextField(
                            value = viewModel.habitName,
                            onValueChange = { viewModel.habitName = it },
                            placeholder = { Text("e.g. Morning Meditation", color = BloomGrayText) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BloomDarkBlue,
                                unfocusedBorderColor = BloomGrayText.copy(alpha = 0.5f)
                            )
                        )

                        // 2. اختيار القسم (Category)
                        Text("Category", fontSize = 12.sp, color = BloomDarkBlue, fontWeight = FontWeight.SemiBold)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            categories.forEach { cat ->
                                val isSelected = viewModel.selectedCategory == cat
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (isSelected) BloomDarkBlue else BloomLightBlue.copy(alpha = 0.6f))
                                        .clickable { viewModel.selectedCategory = cat }
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = cat,
                                        color = if (isSelected) Color.White else BloomDarkBlue,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        // 3. اختيار لون الكارد (Card Appearance)
                        Text("Card Appearance", fontSize = 12.sp, color = BloomDarkBlue, fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            colors.forEach { hex ->
                                val color = Color(android.graphics.Color.parseColor(hex))
                                val isSelected = viewModel.selectedColorHex == hex
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(color)
                                        .border(
                                            width = if (isSelected) 3.dp else 0.dp,
                                            color = if (isSelected) BloomDarkBlue else Color.Transparent,
                                            shape = CircleShape
                                        )
                                        .clickable { viewModel.selectedColorHex = hex }
                                )
                            }
                        }

                        // 4. اختيار وقت التنبيه (Reminder Time)
                        Text("Reminder Time", fontSize = 12.sp, color = BloomDarkBlue, fontWeight = FontWeight.SemiBold)
                        OutlinedTextField(
                            value = viewModel.reminderTime,
                            onValueChange = {},
                            readOnly = true, // يفتح الـ Dialog عند الضغط
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    val calendar = Calendar.getInstance()
                                    TimePickerDialog(
                                        context,
                                        { _, hour, minute ->
                                            val format = if (hour >= 12) "PM" else "AM"
                                            val displayHour = if (hour % 12 == 0) 12 else hour % 12
                                            viewModel.reminderTime = String.format(Locale.US, "%02d:%02d %s", displayHour, minute, format)
                                        },
                                        calendar.get(Calendar.HOUR_OF_DAY),
                                        calendar.get(Calendar.MINUTE),
                                        false
                                    ).show()
                                },
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BloomDarkBlue,
                                unfocusedBorderColor = BloomGrayText.copy(alpha = 0.5f)
                            )
                        )

                        // 5. اختيار أيام التكرار (Repeat Days)
                        Text("Repeat Days", fontSize = 12.sp, color = BloomDarkBlue, fontWeight = FontWeight.SemiBold)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            daysOfWeek.forEach { day ->
                                val isSelected = viewModel.selectedDays.contains(day)
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (isSelected) BloomDarkBlue else BloomLightBlue.copy(alpha = 0.6f))
                                        .clickable { viewModel.toggleDay(day) },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = day,
                                        color = if (isSelected) Color.White else BloomDarkBlue,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                            }
                        }

                        // 6. أزرار الحفظ والإلغاء
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { viewModel.saveHabit(onSuccess = onBackClick) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = BloomDarkBlue),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Save Habit", fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = onBackClick,
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = BloomLightBlue.copy(alpha = 0.7f)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Cancel", color = BloomDarkBlue, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // ثانياً: كارد المعاينة الحية (Live Preview) في أسفل الشاشة
            item {
                val previewColor = Color(android.graphics.Color.parseColor(viewModel.selectedColorHex))
                Text("Live Preview", fontSize = 14.sp, color = BloomGrayText, fontWeight = FontWeight.Medium)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = previewColor)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // أيقونة افتراضية للقسم
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("💡", color = Color.White)
                            }

                            // اسم القسم العلوي الصغير
                            Box(
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(viewModel.selectedCategory, color = Color.White, fontSize = 11.sp)
                            }
                        }

                        Text(
                            text = viewModel.habitName.ifBlank { "Morning Meditation" },
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = "Every weekday at ${viewModel.reminderTime}",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // شريط التوقعات الافتراضي الموضح بالتصميم
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Streak Forecast", color = Color.White.copy(alpha = 0.7f), fontSize = 12.dp.value.sp)
                                Text("85% Likely", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.dp.value.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}