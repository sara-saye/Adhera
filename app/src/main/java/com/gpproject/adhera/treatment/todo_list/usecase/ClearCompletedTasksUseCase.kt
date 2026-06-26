package com.gpproject.adhera.treatment.todo_list.usecase

import com.gpproject.adhera.treatment.todo_list.data.TaskRepository

class ClearCompletedTasksUseCase(
    private val repository: com.gpproject.adhera.treatment.todo_list.data.TaskRepository
) {
    suspend operator fun invoke() {
        repository.clearCompletedTasks()
    }
}
