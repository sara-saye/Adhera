package com.gpproject.adhera.data.usecase

data class TaskUseCases(
    val getAllTasks: GetAllTasksUseCase,
    val getTaskById: GetTaskByIdUseCase,
    val upsertTask: UpsertTaskUseCase,
    val deleteTask: DeleteTaskUseCase
)