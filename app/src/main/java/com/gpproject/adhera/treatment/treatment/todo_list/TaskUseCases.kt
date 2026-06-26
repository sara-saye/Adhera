package com.gpproject.adhera.treatment.treatment.todo_list

data class TaskUseCases(
    val getAllTasks: GetAllTasksUseCase,
    val getTaskById: GetTaskByIdUseCase,
    val upsertTask: UpsertTaskUseCase,
    val deleteTask: DeleteTaskUseCase
)