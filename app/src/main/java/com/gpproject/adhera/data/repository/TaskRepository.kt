package com.gpproject.adhera.data.repository

import com.gpproject.adhera.data.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class TaskRepository(
    // مستقبلاً لما تعملي الـ Database، هتضيفي الـ DAO هنا كدا:
    // private val taskDao: TaskDao
) {

    // 1. جلب كل المهام (هنا بنستخدم IO thread عشان ميعطلش الـ UI)
    suspend fun getAllTasks(): List<Task> = withContext(Dispatchers.IO) {
        // حالياً بيرجع قائمة فاضية، مستقبلاً هيكون: taskDao.getAllTasks()
        emptyList()
    }

    // 2. إضافة مهمة جديدة
    suspend fun insertTask(task: Task) = withContext(Dispatchers.IO) {
        // مستقبلاً: taskDao.insertTask(task)
    }

    // 3. حذف مهمة باستخدام الـ ID الفريد (التربيط اللي عملناه)
    suspend fun deleteTask(taskId: String) = withContext(Dispatchers.IO) {
        // مستقبلاً: taskDao.deleteTaskById(taskId)
    }

    // 4. تحديث مهمة (مهم جداً للـ Re-indexing وتعديل الـ SubTasks)
    suspend fun updateTask(task: Task) = withContext(Dispatchers.IO) {
        // مستقبلاً: taskDao.updateTask(task)
    }

    // 5. جلب مهمة واحدة بالـ ID (عشان سكرينة التعديل)
    suspend fun getTaskById(taskId: String): Task? = withContext(Dispatchers.IO) {
        // مستقبلاً: taskDao.getTaskById(taskId)
        null
    }
}