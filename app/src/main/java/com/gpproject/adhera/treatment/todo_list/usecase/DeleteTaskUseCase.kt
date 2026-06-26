package com.gpproject.adhera.treatment.todo_list.usecase

import com.gpproject.adhera.treatment.todo_list.data.TaskRepository
import com.gpproject.adhera.treatment.todo_list.tododb.TaskEntity

class DeleteTaskUseCase(
    private val repository: com.gpproject.adhera.treatment.todo_list.data.TaskRepository
) {
    suspend operator fun invoke(task: com.gpproject.adhera.treatment.todo_list.tododb.TaskEntity) {
        repository.deleteTask(task)
    }
}