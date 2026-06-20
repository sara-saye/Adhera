package com.gpproject.adhera.data.usecase

import com.gpproject.adhera.data.local.todo.TaskEntity
import com.gpproject.adhera.data.repository.TaskRepository

class GetTaskByIdUseCase(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(taskId: String): TaskEntity? {
        return repository.getTaskById(taskId)
    }
}