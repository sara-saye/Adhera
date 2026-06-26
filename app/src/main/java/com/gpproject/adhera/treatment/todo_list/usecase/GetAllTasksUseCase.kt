package com.gpproject.adhera.treatment.todo_list.usecase

import com.gpproject.adhera.treatment.todo_list.data.TaskRepository
import com.gpproject.adhera.treatment.todo_list.tododb.TaskEntity
import kotlinx.coroutines.flow.Flow

class GetAllTasksUseCase(
    private val repository: com.gpproject.adhera.treatment.todo_list.data.TaskRepository
) {
    operator fun invoke(): Flow<List<com.gpproject.adhera.treatment.todo_list.tododb.TaskEntity>> {
        return repository.getAllTasks()
    }
}