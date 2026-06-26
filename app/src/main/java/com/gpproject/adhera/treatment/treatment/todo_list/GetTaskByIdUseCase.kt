package com.gpproject.adhera.treatment.treatment.todo_list

import com.gpproject.adhera.treatment.treatment.todo_list.tododb.TaskEntity

class GetTaskByIdUseCase(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(taskId: String): TaskEntity? {
        return repository.getTaskById(taskId)
    }
}