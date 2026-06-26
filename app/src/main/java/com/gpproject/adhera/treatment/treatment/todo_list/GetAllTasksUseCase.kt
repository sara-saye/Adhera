package com.gpproject.adhera.treatment.treatment.todo_list

import com.gpproject.adhera.treatment.treatment.todo_list.tododb.TaskEntity
import kotlinx.coroutines.flow.Flow

class GetAllTasksUseCase(
    private val repository: TaskRepository
) {
    operator fun invoke(): Flow<List<TaskEntity>> {
        return repository.getAllTasks()
    }
}