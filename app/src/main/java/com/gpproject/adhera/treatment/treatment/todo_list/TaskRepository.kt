package com.gpproject.adhera.treatment.treatment.todo_list

import com.gpproject.adhera.treatment.treatment.todo_list.tododb.TaskEntity
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getAllTasks(): Flow<List<TaskEntity>>

    suspend fun getTaskById(taskId: String): TaskEntity?

    suspend fun upsertTask(task: TaskEntity)

    suspend fun deleteTask(task: TaskEntity)

    suspend fun clearCompletedTasks()
}