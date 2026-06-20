package com.gpproject.adhera.data.repository

import com.gpproject.adhera.data.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TaskRepository {

    // قائمة وهمية مؤقتة (In-memory) عشان الداتا تظهر في الـ UI لحد ما نربط الـ Room Database
    private val tempTasks = mutableListOf<Task>()

    // 1. جلب كل المهام
    suspend fun getAllTasks(): List<Task> = withContext(Dispatchers.IO) {
        // حالياً بنرجع القائمة المؤقتة، مستقبلاً هيكون: taskDao.getAllTasks()
        tempTasks
    }

    // 2. إضافة مهمة جديدة
    suspend fun insertTask(task: Task) = withContext(Dispatchers.IO) {
        tempTasks.add(task)
        // مستقبلاً: taskDao.insertTask(task)
    }

    // 3. حذف مهمة باستخدام الـ ID
    suspend fun deleteTask(taskId: String) = withContext(Dispatchers.IO) {
        tempTasks.removeIf { it.id == taskId }
        // مستقبلاً: taskDao.deleteTaskById(taskId)
    }

    // 4. تحديث مهمة (مهم جداً للتعديل)
    suspend fun updateTask(task: Task) = withContext(Dispatchers.IO) {
        val index = tempTasks.indexOfFirst { it.id == task.id }
        if (index != -1) {
            tempTasks[index] = task
        }
        // مستقبلاً: taskDao.updateTask(task)
    }

    // 5. جلب مهمة واحدة بالـ ID
    suspend fun getTaskById(taskId: String): Task? = withContext(Dispatchers.IO) {
        // بنبحث في القائمة المؤقتة
        tempTasks.find { it.id == taskId }
        // مستقبلاً: taskDao.getTaskById(taskId)
    }
}