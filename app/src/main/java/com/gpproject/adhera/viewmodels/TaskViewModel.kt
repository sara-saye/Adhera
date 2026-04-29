package com.gpproject.adhera.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.gpproject.adhera.data.model.SubTask
import com.gpproject.adhera.data.model.Task
import com.gpproject.adhera.data.repository.TaskRepository
import kotlinx.coroutines.launch
import java.util.UUID

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = "YOUR_API_KEY"
    )

    var generatedSubTasks = mutableStateListOf<SubTask>()
    var editingSubTasks = mutableStateListOf<SubTask>()

    fun getTaskById(taskId: String): Task? {
        return null
    }

    fun splitTaskWithGemini(description: String) {
        if (description.length < 20) return
        viewModelScope.launch {
            try {
                val prompt = "Split this task into steps: $description"
                val response = generativeModel.generateContent(prompt)
                val steps = response.text?.split("\n")?.filter { it.isNotBlank() } ?: emptyList()

                generatedSubTasks.clear()
                steps.forEach { step ->
                    generatedSubTasks.add(SubTask(title = step.trim()))
                }
            } catch (e: Exception) {}
        }
    }

    fun saveTask(title: String, description: String, duration: String, priority: String, startDate: String, startTime: String, focusTime: String, reminder: Boolean) {
        viewModelScope.launch {
            val newTask = Task(
                title = title,
                description = description,
                durationType = duration,
                priority = priority,
                startDate = startDate,
                startTime = startTime,
                focusTime = focusTime,
                reminderEnabled = reminder,
                subTasks = generatedSubTasks.toList()
            )
            repository.insertTask(newTask)
        }
    }

    fun updateTask(
        taskId: String,
        newTitle: String,
        newDescription: String,
        isReminderEnabled: Boolean,
        priority: String,
        duration: String,
        startDate: String,
        startTime: String,
        focusTime: String
    ) {
        viewModelScope.launch {
            val updatedTask = Task(
                id = taskId,
                title = newTitle,
                description = newDescription,
                reminderEnabled = isReminderEnabled,
                priority = priority,
                durationType = duration,
                startDate = startDate,
                startTime = startTime,
                focusTime = focusTime,
                subTasks = editingSubTasks.toList()
            )
            repository.updateTask(updatedTask)
        }
    }

    fun loadSubTasksForEditing(subTasks: List<SubTask>) {
        editingSubTasks.clear()
        editingSubTasks.addAll(subTasks)
    }
}