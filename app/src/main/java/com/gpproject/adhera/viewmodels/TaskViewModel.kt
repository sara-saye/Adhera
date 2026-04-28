package com.gpproject.adhera.viewmodels

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.gpproject.adhera.data.model.SubTask
import com.gpproject.adhera.data.model.Task
import com.google.ai.client.generativeai.type.content
import com.gpproject.adhera.data.model.TaskPriority
import kotlinx.coroutines.launch
import java.util.UUID

class TaskViewModel : ViewModel() {

    // 1. إعداد موديل Gemini (استبدلي YOUR_API_KEY بمفتاحك الخاص)
    private val generativeModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = "YOUR_API_KEY"
    )

    // 2. State لإدارة المهام الفرعية (الإضافة والتعديل)
    var generatedSubTasks = mutableStateListOf<SubTask>()
        private set

    var editingSubTasks = mutableStateListOf<SubTask>()
        private set

    // --- عمليات الـ Logic الخاصة بالمهام ---

    // جلب مهمة معينة بالـ ID (مبدئياً من قائمة وهمية أو داتابيز)
    fun getTaskById(taskId: String): Task? {
        // هنا المفروض تنادي على الـ Repository
        // سأعيد كائن وهمي للتجربة فقط
        return null
    }

    // تقسيم المهمة باستخدام الذكاء الاصطناعي
    fun splitTaskWithGemini(description: String) {
        viewModelScope.launch {
            try {
                val prompt = """
                    I have a task description: "$description". 
                    Please split it into small, actionable daily steps. 
                    Return the result as a simple list of strings, each step on a new line. 
                    No introduction, just the steps.
                """.trimIndent()

                val response = generativeModel.generateContent(prompt)
                val steps = response.text?.split("\n")?.filter { it.isNotBlank() } ?: emptyList()

                generatedSubTasks.clear()
                steps.forEach { step ->
                    generatedSubTasks.add(
                        SubTask(
                            id = UUID.randomUUID().toString(),
                            title = step.replace(Regex("^[-*\\d.]+\\s*"), ""), // تنظيف النص من الأرقام أو النقط
                            date = "Today"
                        )
                    )
                }
            } catch (e: Exception) {
                // هندلة الأخطاء هنا
            }
        }
    }

    // إعادة تقسيم المهمة (Regenerate) في شاشة التعديل
    fun regenerateSubTasks(description: String) {
        editingSubTasks.clear()
        splitTaskWithGemini(description)
        // بعد التوليد، ننقلهم لقائمة التعديل
        viewModelScope.launch {
            // ننتظر قليلاً للتأكد من انتهاء الـ AI (أو نربطها بـ State)
            editingSubTasks.addAll(generatedSubTasks)
        }
    }

    // --- إدارة الـ Sub-Tasks (الـ IDs هنا هي الأساس) ---

    fun loadSubTasksForEditing(subTasks: List<SubTask>) {
        editingSubTasks.clear()
        editingSubTasks.addAll(subTasks)
    }

    fun addNewSubTask() {
        val newStep = SubTask(
            id = UUID.randomUUID().toString(),
            title = "",
            date = "New Step"
        )
        editingSubTasks.add(newStep)
    }

    fun removeSubTask(subTaskId: String) {
        editingSubTasks.removeIf { it.id == subTaskId }
    }

    fun updateSubTaskName(subTaskId: String, newName: String) {
        val index = editingSubTasks.indexOfFirst { it.id == subTaskId }
        if (index != -1) {
            editingSubTasks[index] = editingSubTasks[index].copy(title = newName)
        }
    }

    // تحديث عنوان الخطوة في شاشة الإضافة
    fun updateSubTaskTitle(subTaskId: String, newTitle: String) {
        val index = generatedSubTasks.indexOfFirst { it.id == subTaskId }
        if (index != -1) {
            generatedSubTasks[index] = generatedSubTasks[index].copy(title = newTitle)
        }
    }

    // الحفظ النهائي للمهمة (Update/Insert)
    fun updateTask(
        taskId: String,
        newTitle: String,
        newDescription: String,
        isReminderEnabled: Boolean
    ) {
        viewModelScope.launch {
            // هنا نجمع البيانات النهائية ونبعتها للـ Repository
            val updatedTask = Task(
                id = taskId,
                title = newTitle,
                description = newDescription,
                reminderEnabled = isReminderEnabled,
                subTasks = editingSubTasks.toList(),
                startDate = 0L, // كملي باقي البيانات حسب الحاجة
                endDate = 0L,
                startTime = "",
                endTime = "",
                isMultiDay = false
            )
            // repository.updateTask(updatedTask)
        }
    }
}