package com.gpproject.adhera.treatment.treatment.todo_list

import com.gpproject.adhera.treatment.treatment.todo_list.tododb.TaskEntity

class DeleteTaskUseCase(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: TaskEntity) {
        repository.deleteTask(task)
    }
}