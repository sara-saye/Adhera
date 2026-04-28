package com.gpproject.adhera.data.model

import java.util.UUID
data class SubTask(
    val id: String = UUID.randomUUID().toString(),
    var title: String,
    var isDone: Boolean = false,
    val date: String
)

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val priority: TaskPriority = TaskPriority.LOW,
    val startDate: Long,
    val endDate: Long,
    val startTime: String,
    val endTime: String,
    val isMultiDay: Boolean,
    val subTasks: List<SubTask> = listOf(),
    val reminderEnabled: Boolean = false,
    val isDone: Boolean = false // ضفت دي عشان الـ Checkbox اللي في الـ Dashboard
)

enum class TaskPriority {
    LOW, MEDIUM, HIGH
}