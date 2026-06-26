package com.gpproject.adhera.treatment.todo_list.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gpproject.adhera.treatment.todo_list.data.TaskManagerRepository
import com.gpproject.adhera.treatment.todo_list.data.TaskManagerRepositoryImpl
import com.gpproject.adhera.treatment.todo_list.usecase.TaskUseCases

class TaskViewModelFactory(
    private val taskUseCases: com.gpproject.adhera.treatment.todo_list.usecase.TaskUseCases,
    private val taskManagerRepository: com.gpproject.adhera.treatment.todo_list.data.TaskManagerRepository = _root_ide_package_.com.gpproject.adhera.treatment.todo_list.data.TaskManagerRepositoryImpl()
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(_root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.TaskViewModel::class.java)) {
            return _root_ide_package_.com.gpproject.adhera.treatment.todo_list.screens.TaskViewModel(
                taskUseCases,
                taskManagerRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
