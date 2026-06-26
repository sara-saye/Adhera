package com.gpproject.adhera.treatment.treatment.todo_list

import com.gpproject.adhera.treatment.treatment.todo_list.tododb.TaskDao
import com.gpproject.adhera.treatment.treatment.todo_list.tododb.TaskEntity
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