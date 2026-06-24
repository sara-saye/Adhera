package com.gpproject.adhera.data.repository


import com.gpproject.adhera.data.local.habit.HabitDao
import com.gpproject.adhera.data.model.Habit
import kotlinx.coroutines.flow.Flow

class HabitRepository(private val habitDao: HabitDao) {

    val allHabits: Flow<List<Habit>> = habitDao.getAllHabits()

    suspend fun insert(habit: Habit) {
        habitDao.insertHabit(habit)
    }

    suspend fun update(habit: Habit) {
        habitDao.updateHabit(habit)
    }

    // دالة مخصصة لعمل Toggle (إنجاز / إلغاء إنجاز) وتحديث الـ Streak تلقائياً
    suspend fun toggleHabitCompletion(habit: Habit) {
        val newStatus = !habit.isCompletedToday
        val newStreak = if (newStatus) habit.currentStreak + 1 else maxOf(0, habit.currentStreak - 1)
        habitDao.updateHabitStatus(habit.id, newStatus, newStreak)
    }

    suspend fun delete(habit: Habit) {
        habitDao.deleteHabit(habit)
    }
}