package com.gpproject.adhera.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

// تعريف واجهات التنقل المطلوبة لعمل الـ Bottom Bar
sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Today : Screen("reminders_screen", "اليوم", Icons.Default.CheckCircle)
    object Habits : Screen("new_habit_route", "العادات", Icons.Default.DateRange)
    object Stats : Screen("performance_analytics", "الإحصائيات", Icons.Default.Star)
    object Profile : Screen("profile", "الملف الشخصي", Icons.Default.Person)
}