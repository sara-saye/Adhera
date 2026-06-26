package com.gpproject.adhera.treatment.treatment.habit_tracker.habitdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.gpproject.adhera.treatment.treatment.habit_tracker.Habit
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    // جلب كل العادات وترتيبها من الأحدث للأقدم
    @Query("SELECT * FROM habits ORDER BY createdAt DESC")
    fun getAllHabits(): Flow<List<Habit>>

    // إضافة عادة جديدة أو تعديلها
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertHabit(habit: Habit)

    @Update
    suspend fun updateHabit(habit: Habit)

    @Delete
    suspend fun deleteHabit(habit: Habit)

    // تحديث حالة العادة عند الضغط على زر الإنجاز (الدائرة)
    @Query("UPDATE habits SET isCompletedToday = :isCompleted, currentStreak = :newStreak WHERE id = :habitId")
    suspend fun updateHabitStatus(habitId: Int, isCompleted: Boolean, newStreak: Int)
}