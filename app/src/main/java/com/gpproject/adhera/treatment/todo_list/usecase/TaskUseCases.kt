package com.gpproject.adhera.treatment.todo_list.usecase

data class TaskUseCases(
    val getAllTasks: com.gpproject.adhera.treatment.todo_list.usecase.GetAllTasksUseCase,
    val getTaskById: com.gpproject.adhera.treatment.todo_list.usecase.GetTaskByIdUseCase,
    val upsertTask: com.gpproject.adhera.treatment.todo_list.usecase.UpsertTaskUseCase,
    val deleteTask: com.gpproject.adhera.treatment.todo_list.usecase.DeleteTaskUseCase,
    val clearCompletedTasks: com.gpproject.adhera.treatment.todo_list.usecase.ClearCompletedTasksUseCase
)
