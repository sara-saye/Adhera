package com.gpproject.adhera.data.usecase

import com.gpproject.adhera.data.local.todo.TaskEntity
import com.gpproject.adhera.data.repository.TaskRepository

class UpsertTaskUseCase(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: TaskEntity) {
        repository.upsertTask(task)
    }
}