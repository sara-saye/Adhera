package com.gpproject.adhera.treatment.todo_list.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gpproject.adhera.treatment.todo_list.data.TaskManagerRepository
import com.gpproject.adhera.treatment.todo_list.data.TaskManagerRepositoryImpl
import com.gpproject.adhera.treatment.todo_list.tododb.TaskEntity
import com.gpproject.adhera.treatment.todo_list.usecase.TaskUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.collections.orEmpty

class TaskViewModel(
    private val taskUseCases: com.gpproject.adhera.treatment.todo_list.usecase.TaskUseCases,
    private val taskManagerRepository: com.gpproject.adhera.treatment.todo_list.data.TaskManagerRepository = _root_ide_package_.com.gpproject.adhera.treatment.todo_list.data.TaskManagerRepositoryImpl()
) : ViewModel() {

    private val _tasksState = MutableStateFlow<List<com.gpproject.adhera.treatment.todo_list.tododb.TaskEntity>>(emptyList())
    val tasksState: StateFlow<List<com.gpproject.adhera.treatment.todo_list.tododb.TaskEntity>> = _tasksState.asStateFlow()

    private val _currentTaskState = MutableStateFlow<com.gpproject.adhera.treatment.todo_list.tododb.TaskEntity?>(null)
    val currentTaskState: StateFlow<com.gpproject.adhera.treatment.todo_list.tododb.TaskEntity?> = _currentTaskState.asStateFlow()

    private val _isGeneratingMilestones = MutableStateFlow(false)
    val isGeneratingMilestones: StateFlow<Boolean> = _isGeneratingMilestones.asStateFlow()

    private val _aiErrorState = MutableStateFlow<String?>(null)
    val aiErrorState: StateFlow<String?> = _aiErrorState.asStateFlow()

    init {
        getAllTasks()
    }

    private fun getAllTasks() {
        viewModelScope.launch {
            taskUseCases.getAllTasks().collect { tasks ->
                _tasksState.value = tasks
            }
        }
    }

    fun loadTaskById(taskId: String) {
        viewModelScope.launch {
            _currentTaskState.value = taskUseCases.getTaskById(taskId)
        }
    }

    fun upsertTask(task: com.gpproject.adhera.treatment.todo_list.tododb.TaskEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            taskUseCases.upsertTask(task)
            onComplete()
        }
    }

    fun deleteTask(task: com.gpproject.adhera.treatment.todo_list.tododb.TaskEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            taskUseCases.deleteTask(task)
            onComplete()
        }
    }

    fun clearCompletedTasks() {
        viewModelScope.launch {
            taskUseCases.clearCompletedTasks()
        }
    }

    fun toggleTaskCompletion(task: com.gpproject.adhera.treatment.todo_list.tododb.TaskEntity) {
        viewModelScope.launch {
            taskUseCases.upsertTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun generateAiMilestones(
        title: String,
        description: String,
        onGenerated: (List<String>) -> Unit
    ) {
        val promptTitle = title.ifBlank { "Untitled task" }
        val promptDescription = description.ifBlank { promptTitle }

        viewModelScope.launch {
            _isGeneratingMilestones.value = true
            _aiErrorState.value = null

            val steps = taskManagerRepository
                .splitTask(promptTitle, promptDescription)
                ?.toMilestoneList()
                .orEmpty()

            if (steps.isEmpty()) {
                _aiErrorState.value = "Could not generate steps. Add a clearer description and try again."
            } else {
                onGenerated(steps)
            }

            _isGeneratingMilestones.value = false
        }
    }

    fun clearCurrentTask() {
        _currentTaskState.value = null
    }

    fun clearAiError() {
        _aiErrorState.value = null
    }

    private fun String.toMilestoneList(): List<String> {
        return replace("[", "\n")
            .replace("]", "\n")
            .replace("\"", "")
            .replace(",", "\n")
            .lineSequence()
            .map { line ->
                line.trim()
                    .removePrefix("-")
                    .removePrefix("*")
                    .replace(Regex("^\\d+[.)-]\\s*"), "")
                    .replace(Regex("^\\[[ xX]]\\s*"), "")
                    .replace(Regex("^(step|task)\\s*\\d*\\s*[:.-]\\s*", RegexOption.IGNORE_CASE), "")
                    .trim()
            }
            .filter { it.length > 2 && !it.equals("subtasks", ignoreCase = true) }
            .distinct()
            .take(8)
            .toList()
    }
}
