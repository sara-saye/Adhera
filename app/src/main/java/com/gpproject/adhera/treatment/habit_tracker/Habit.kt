package com.gpproject.adhera.treatment.habit_tracker

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,             // اسم العادة (مثل: Deep Hydration)
    val category: String,         // القسم (Mind, Body, Focus)
    val colorHex: String,         // لون الكارد المختار
    val reminderTime: String,     // وقت التنبيه (08:00 AM)
    val repeatDays: String,       // الأيام المحددة لتكرار العادة (M, W, F)
    val isCompletedToday: Boolean = false, // هل تم إنجازها اليوم؟
    val currentStreak: Int = 0,   // العداد المتتالي للأيام (Streak)
    val totalCompletions: Int = 0, // إجمالي مرات الإنجاز للإحصائيات
    val createdAt: Long = System.currentTimeMillis()
)