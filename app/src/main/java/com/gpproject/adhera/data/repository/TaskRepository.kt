package com.gpproject.adhera.data.repository

import com.gpproject.adhera.data.local.todo.TaskEntity
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getAllTasks(): Flow<List<TaskEntity>>

    suspend fun getTaskById(taskId: String): TaskEntity?

    suspend fun upsertTask(task: TaskEntity)

    suspend fun deleteTask(task: TaskEntity)

    suspend fun clearCompletedTasks()
}