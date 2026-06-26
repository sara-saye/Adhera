package com.gpproject.adhera.treatment.treatment.todo_list.tododb


import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val durationType: String, // "Today", "Week", "Month", "Custom"
    val startDate: String,    // هنخزن التاريخ كـ String عشان يطابق الـ UI عندك
    val endDate: String?,     // بيكون Null لو الـ durationType مش "Custom"
    val startTime: String,    // زي "09:00 AM"
    val dailyFocus: String,   // زي "02:30 HRS"
    val priority: String,     // "High", "Medium", "Low"
    val isCompleted: Boolean = false,
    val reminderEnabled: Boolean = true,

    // لستة الخطوات اللي الـ AI هيعملها أو الـ Sub-tasks اللي اليوزر هيضيفها
    val milestones: List<String> = emptyList()
)