package com.gpproject.adhera.data.repository

import com.gpproject.adhera.data.local.todo.TaskDao
import com.gpproject.adhera.data.local.todo.TaskEntity
import com.gpproject.adhera.data.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class TaskRepositoryImpl(
    private val dao: TaskDao
) : TaskRepository {

    override fun getAllTasks(): Flow<List<TaskEntity>> {
        return dao.getAllTasks()
    }

    override suspend fun getTaskById(taskId: String): TaskEntity? {
        return dao.getTaskById(taskId)
    }

    override suspend fun upsertTask(task: TaskEntity) {
        dao.upsertTask(task)
    }

    override suspend fun deleteTask(task: TaskEntity) {
        dao.deleteTask(task)
    }

    override suspend fun clearCompletedTasks() {
        dao.clearCompletedTasks()
    }
}