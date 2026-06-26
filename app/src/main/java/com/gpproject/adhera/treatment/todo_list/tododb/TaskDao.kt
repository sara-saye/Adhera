package com.gpproject.adhera.treatment.todo_list.tododb


import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    // بنستخدم Flow عشان الـ UI يتحدث تلقائي أول ما أي حاجة تتغير في الداتابيز
    @Query("SELECT * FROM tasks")
    fun getAllTasks(): Flow<List<com.gpproject.adhera.treatment.todo_list.tododb.TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    suspend fun getTaskById(taskId: String): com.gpproject.adhera.treatment.todo_list.tododb.TaskEntity?

    // OnConflictStrategy.REPLACE عشان لو الداتا موجودة قبل كده يعملها Update (تعديل) ولو مش موجودة يعملها Insert (حفظ)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTask(task: com.gpproject.adhera.treatment.todo_list.tododb.TaskEntity)

    @Delete
    suspend fun deleteTask(task: com.gpproject.adhera.treatment.todo_list.tododb.TaskEntity)

    // كويري سريع لو حابة تمسحي الحاجات الـ Completed بضغطة زرار من الـ UI عندك
    @Query("DELETE FROM tasks WHERE isCompleted = 1")
    suspend fun clearCompletedTasks()
}