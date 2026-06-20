package com.gpproject.adhera.data.usecase

import com.gpproject.adhera.data.local.todo.TaskEntity
import com.gpproject.adhera.data.repository.TaskRepository

class DeleteTaskUseCase(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: TaskEntity) {
        repository.deleteTask(task)
    }
}