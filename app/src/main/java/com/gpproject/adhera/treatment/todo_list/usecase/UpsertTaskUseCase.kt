package com.gpproject.adhera.treatment.todo_list.usecase

import com.gpproject.adhera.treatment.todo_list.data.TaskRepository
import com.gpproject.adhera.treatment.todo_list.tododb.TaskEntity

class UpsertTaskUseCase(
    private val repository: com.gpproject.adhera.treatment.todo_list.data.TaskRepository
) {
    suspend operator fun invoke(task: com.gpproject.adhera.treatment.todo_list.tododb.TaskEntity) {
        repository.upsertTask(task)
    }
}