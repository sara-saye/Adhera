package com.gpproject.adhera.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gpproject.adhera.data.local.todo.TaskEntity
import com.gpproject.adhera.data.usecase.TaskUseCases
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskViewModel(
    private val taskUseCases: TaskUseCases
) : ViewModel() {

    // 1. State لشاشة الـ TodoListScreen (كل التاسكات)
    private val _tasksState = MutableStateFlow<List<TaskEntity>>(emptyList())
    val tasksState: StateFlow<List<TaskEntity>> = _tasksState.asStateFlow()

    // 2. State لشاشة الـ TaskDetailsScreen و EditTaskScreen (التاسك المحددة حالياً)
    private val _currentTaskState = MutableStateFlow<TaskEntity?>(null)
    val currentTaskState: StateFlow<TaskEntity?> = _currentTaskState.asStateFlow()

    init {
        // أول ما الـ ViewModel يشتغل، بنبدأ نراقب الداتابيز علطول
        getAllTasks()
    }

    // جلب كل التاسكات (تحديث تلقائي بفضل الـ Flow)
    private fun getAllTasks() {
        viewModelScope.launch {
            taskUseCases.getAllTasks().collect { tasks ->
                _tasksState.value = tasks
            }
        }
    }

    // جلب بيانات تاسك معينة بالـ ID لشاشات الـ Details والـ Edit
    fun loadTaskById(taskId: String) {
        viewModelScope.launch {
            val task = taskUseCases.getTaskById(taskId)
            _currentTaskState.value = task
        }
    }

    // حفظ أو تعديل تاسك (تستخدم في Create و Edit)
    fun upsertTask(task: TaskEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            taskUseCases.upsertTask(task)
            onComplete() // Callback عشان نقفل الشاشة بعد الحفظ ونرجع لورا
        }
    }

    // مسح التاسك
    fun deleteTask(task: TaskEntity, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            taskUseCases.deleteTask(task)
            onComplete()
        }
    }

    // تحديث حالة الـ Checkbox بسرعة من الـ ToDoListScreen
    fun toggleTaskCompletion(task: TaskEntity) {
        viewModelScope.launch {
            val updatedTask = task.copy(isCompleted = !task.isCompleted)
            taskUseCases.upsertTask(updatedTask)
        }
    }

    // فانكشن وهمية (Mock) لزرار الـ AI Generate لحد ما نربط الـ API بتاعه
    fun generateAiMilestones(description: String, onGenerated: (List<String>) -> Unit) {
        viewModelScope.launch {
            // هنا بعدين هتحطي نداء الـ API أو السيرفيس الخاصة بالـ ADHD تفكيك المهام
            // حالياً هنعمل حاجة Mock سريعة للتجربة:
            val mockMilestones = listOf(
                "Break description into micro-tasks",
                "Set a 5-minute timer to start",
                "Remove all workspace distractions"
            )
            onGenerated(mockMilestones)
        }
    }

    // تنظيف الـ currentTask عند الخروج من شاشات التفاصيل
    fun clearCurrentTask() {
        _currentTaskState.value = null
    }
}