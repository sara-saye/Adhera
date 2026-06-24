package com.gpproject.adhera.ui.screens.treatment.habit_tracker.stats
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gpproject.adhera.data.local.habit.HabitServiceLocator
import com.gpproject.adhera.ui.screens.treatment.habit_tracker.AdheraViewModelFactory

//import com.gpproject.adhera.ui.common.AdheraViewModelFactory

// ألوان شاشة الإحصائيات المأخوذة من الصورة image_a96aae.png
val BloomDarkBlue = Color(0xFF032B43)
val BloomLightBlue = Color(0xFFBBE1FA)
val BloomActiveBlue = Color(0xFF3282B8)
val BloomBackground = Color(0xFFF7F9FC)
val BloomCardBg = Color(0xFFFFFFFF)
val BloomGrayText = Color(0xFF7F8C8D)
val BloomHeatmapDark = Color(0xFF0B3C5D)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerformanceAnalyticsScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // ✅ استخدام الـ Repository من ServiceLocator (بدون أي تسجيل في Manifest)
    val repository = remember { HabitServiceLocator.getRepository(context) }

    val viewModel: StatsViewModel = viewModel(
        factory = AdheraViewModelFactory(repository)
    )

    val habitsList by viewModel.habitsList.collectAsState()
    val analyticsState by viewModel.analyticsState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Bloom", color = BloomDarkBlue, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Menu, contentDescription = null, tint = BloomDarkBlue)
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(BloomLightBlue)
                    )
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

            // 1. عنوان الشاشة العلوي والنص التوضيحي
            item {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(
                        text = "Performance\nAnalytics",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = BloomDarkBlue,
                        lineHeight = 34.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tracking your progress and consistency\nacross all active habits.",
                        fontSize = 14.sp,
                        color = BloomGrayText
                    )
                }
            }

            // 2. كارد الكفاءة العامة (Overall Completion)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BloomCardBg)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Overall Completion",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = BloomDarkBlue,
                            modifier = Modifier.align(Alignment.Start)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // المؤشر الدائري للكفاءة
                        val animatedEff by animateFloatAsState(targetValue = analyticsState.efficiencyPercentage / 100f, label = "eff")
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(130.dp)) {
                            Canvas(modifier = Modifier.size(130.dp)) {
                                drawCircle(color = BloomLightBlue.copy(alpha = 0.4f), style = Stroke(width = 14.dp.toPx()))
                                drawArc(
                                    color = BloomDarkBlue,
                                    startAngle = -90f,
                                    sweepAngle = 360f * animatedEff,
                                    useCenter = false,
                                    style = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("${analyticsState.efficiencyPercentage}%", fontSize = 30.sp, fontWeight = FontWeight.Bold, color = BloomDarkBlue)
                                Text("EFFICIENCY", fontSize = 10.sp, color = BloomGrayText, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        Divider(color = BloomGrayText.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(12.dp))

                        // الأرقام السفلية (Active Habits & Daily Average)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("Active Habits", fontSize = 12.sp, color = BloomGrayText)
                                Text("${analyticsState.activeHabitsCount}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = BloomDarkBlue)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Daily Average", fontSize = 12.sp, color = BloomGrayText)
                                Text(String.format("%.1f", analyticsState.dailyAverage), fontSize = 20.sp, fontWeight = FontWeight.Bold, color = BloomDarkBlue)
                            }
                        }
                    }
                }
            }

            // 3. كارد الـ Consistency Heatmap
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BloomCardBg)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Consistency Heatmap", fontWeight = FontWeight.Bold, color = BloomDarkBlue)
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                SuggestionChip(onClick = {}, label = { Text("7 Days", fontSize = 11.sp) })
                                SuggestionChip(onClick = {}, label = { Text("30 Days", fontSize = 11.sp) })
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // مربعات الالتزام الافتراضية لكل يوم في الأسبوع
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            val days = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
                            val colorsList = listOf(BloomActiveBlue, BloomDarkBlue, BloomLightBlue, BloomActiveBlue, Color(0xFFE5E9F0), Color(0xFFE5E9F0), Color(0xFFE5E9F0))

                            days.forEachIndexed { idx, day ->
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(day, fontSize = 11.sp, color = BloomGrayText)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Box(modifier = Modifier.size(32.dp).clip(RoundedCornerShape(6.dp)).background(colorsList[idx]))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // نص التلميحات الذكي الذكي الموضح أسفل الـ Heatmap
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(BloomBackground, RoundedCornerShape(8.dp))
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("💡", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Your consistency peak is typically on Tuesdays. Consider scheduling your most difficult tasks for this window.",
                                    fontSize = 11.sp,
                                    color = BloomDarkBlue,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }

            // 4. جزء نسب تقدم كل عادة بشكل منفصل (Individual Habit Progress)
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Individual Habit Progress", fontWeight = FontWeight.Bold, color = BloomDarkBlue, fontSize = 16.sp)
                    TextButton(onClick = {}) {
                        Text("View All", color = BloomDarkBlue)
                        Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp))
                    }
                }
            }

            // جلب العادات الفعلية وعرض مؤشر تقدم لكل واحدة
            if (habitsList.isEmpty()) {
                // عرض عادات افتراضية تطابق الصورة تماماً في حال عدم وجود عادات مضافة
                item { DummyHabitProgressRow("Deep Work Session", 0.92f, "92%") }
                item { DummyHabitProgressRow("Hydration Target", 0.75f, "75%") }
                item { DummyHabitProgressRow("Strength Training", 0.60f, "60%") }
                item { DummyHabitProgressRow("Mindful Meditation", 0.45f, "45%") }
            } else {
                items(habitsList) { habit ->
                    // حساب النسبة الفردية بناءً على إجمالي المرات التخيلي للتجربة
                    val progressFloat = if (habit.isCompletedToday) 0.85f else 0.40f
                    val progressText = if (habit.isCompletedToday) "85%" else "40%"
                    DummyHabitProgressRow(habit.name, progressFloat, progressText)
                }
            }

            // 5. كارد التحدي والـ Streak القادم في أسفل الشاشة (Next Milestone)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = BloomHeatmapDark)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("🏆  NEXT MILESTONE", fontSize = 11.sp, color = Color.White.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("30-Day Completion Streak", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "You are only 4 days away from achieving your 'Resilient Performer' badge. Keep the momentum!",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}

// عنصر مخصص لرسم صف العادة الفردية ومؤشر تقدمها الأفقي
@Composable
fun DummyHabitProgressRow(title: String, progress: Float, percentText: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = BloomCardBg),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(title, fontWeight = FontWeight.SemiBold, color = BloomDarkBlue, fontSize = 14.sp)
                Text(percentText, fontWeight = FontWeight.Bold, color = BloomDarkBlue, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                color = BloomDarkBlue,
                trackColor = BloomLightBlue.copy(alpha = 0.4f)
            )
        }
    }
}