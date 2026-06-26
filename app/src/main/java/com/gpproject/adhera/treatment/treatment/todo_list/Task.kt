package com.gpproject.adhera.treatment.treatment.todo_list

import java.util.UUID

data class SubTask(
    val id: String = UUID.randomUUID().toString(),
    var title: String,
    var isDone: Boolean = false,
    val date: String = "Today"
)

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val priority: String,
    val durationType: String,
    val startDate: String,
    val endDate: String? = null,
    val startTime: String,
    val focusTime: String,
    val reminderEnabled: Boolean = false,
    val subTasks: List<SubTask> = listOf(),
    val isDone: Boolean = false
)