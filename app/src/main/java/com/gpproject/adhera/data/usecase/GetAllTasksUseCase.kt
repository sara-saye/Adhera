package com.gpproject.adhera.data.usecase

import com.gpproject.adhera.data.local.todo.TaskEntity
import com.gpproject.adhera.data.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class GetAllTasksUseCase(
    private val repository: TaskRepository
) {
    operator fun invoke(): Flow<List<TaskEntity>> {
        return repository.getAllTasks()
    }
}