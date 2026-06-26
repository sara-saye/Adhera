package com.gpproject.adhera.treatment.todo_list.data

import com.gpproject.adhera.treatment.todo_list.tododb.TaskDao
import com.gpproject.adhera.treatment.todo_list.tododb.TaskEntity
import kotlinx.coroutines.flow.Flow

class TaskRepositoryImpl(
    private val dao: com.gpproject.adhera.treatment.todo_list.tododb.TaskDao
) : com.gpproject.adhera.treatment.todo_list.data.TaskRepository {

    override fun getAllTasks(): Flow<List<com.gpproject.adhera.treatment.todo_list.tododb.TaskEntity>> {
        return dao.getAllTasks()
    }

    override suspend fun getTaskById(taskId: String): com.gpproject.adhera.treatment.todo_list.tododb.TaskEntity? {
        return dao.getTaskById(taskId)
    }

    override suspend fun upsertTask(task: com.gpproject.adhera.treatment.todo_list.tododb.TaskEntity) {
        dao.upsertTask(task)
    }

    override suspend fun deleteTask(task: com.gpproject.adhera.treatment.todo_list.tododb.TaskEntity) {
        dao.deleteTask(task)
    }

    override suspend fun clearCompletedTasks() {
        dao.clearCompletedTasks()
    }
}