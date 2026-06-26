package com.gpproject.adhera.treatment.todo_list.usecase

import com.gpproject.adhera.treatment.todo_list.data.TaskRepository
import com.gpproject.adhera.treatment.todo_list.tododb.TaskEntity

class GetTaskByIdUseCase(
    private val repository: com.gpproject.adhera.treatment.todo_list.data.TaskRepository
) {
    suspend operator fun invoke(taskId: String): com.gpproject.adhera.treatment.todo_list.tododb.TaskEntity? {
        return repository.getTaskById(taskId)
    }
}