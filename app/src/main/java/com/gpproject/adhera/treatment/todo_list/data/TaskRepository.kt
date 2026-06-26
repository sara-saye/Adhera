package com.gpproject.adhera.treatment.todo_list.data

import com.gpproject.adhera.treatment.todo_list.tododb.TaskEntity
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getAllTasks(): Flow<List<com.gpproject.adhera.treatment.todo_list.tododb.TaskEntity>>

    suspend fun getTaskById(taskId: String): com.gpproject.adhera.treatment.todo_list.tododb.TaskEntity?

    suspend fun upsertTask(task: com.gpproject.adhera.treatment.todo_list.tododb.TaskEntity)

    suspend fun deleteTask(task: com.gpproject.adhera.treatment.todo_list.tododb.TaskEntity)

    suspend fun clearCompletedTasks()
}