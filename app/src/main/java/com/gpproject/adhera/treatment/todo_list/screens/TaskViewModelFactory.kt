package com.gpproject.adhera.treatment.todo_list.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gpproject.adhera.treatment.todo_list.data.TaskManagerRepository
import com.gpproject.adhera.treatment.todo_list.data.TaskManagerRepositoryImpl
import com.gpproject.adhera.treatment.todo_list.usecase.TaskUseCases

class TaskViewModelFactory(
    private val taskUseCases: TaskUseCases,
    private val taskManagerRepository: TaskManagerRepository = TaskManagerRepositoryImpl()
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            return TaskViewModel(
                taskUseCases,
                taskManagerRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}