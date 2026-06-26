package com.gpproject.adhera.treatment.treatment.todo_list

import com.gpproject.adhera.treatment.treatment.todo_list.tododb.TaskEntity

class UpsertTaskUseCase(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: TaskEntity) {
        repository.upsertTask(task)
    }
}