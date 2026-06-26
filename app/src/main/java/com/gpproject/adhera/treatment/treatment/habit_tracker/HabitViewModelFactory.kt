package com.gpproject.adhera.ui.screens.treatment.habit_tracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gpproject.adhera.treatment.treatment.habit_tracker.HabitRepository
import com.gpproject.adhera.treatment.treatment.habit_tracker.habits.NewHabitViewModel
import com.gpproject.adhera.treatment.treatment.habit_tracker.reminders.RemindersViewModel
import com.gpproject.adhera.treatment.treatment.habit_tracker.stats.StatsViewModel
import kotlin.jvm.java

///import com.gpproject.adhera.ui.screens.habits.NewHabitViewModel
//import com.gpproject.adhera.ui.screens.reminders.RemindersViewModel
//import com.gpproject.adhera.ui.screens.stats.StatsViewModel

/**
 * Factory موحّد لكل الـ ViewModels في التطبيق.
 *
 * المشكلة اللي كانت موجودة قبل كذلك: كل ViewModel كان له Factory مستقل
 * (RemindersViewModelFactory, StatsViewModelFactory, TodayViewModelFactory,
 * NewHabitViewModelFactory) بنفس الكود متكرر 4 مرات. ده عمل تكرار غير ضروري
 * وكان سهل يحصل فيه نسيان تحديث factory لو ViewModel جديد اتضاف.
 *
 * هنا factory واحد بس بيغطي كل الـ ViewModels، وبيستقبل الـ Repository
 * مرة واحدة من الخارج (Dependency Injection يدوي بدون Hilt).
 */
class AdheraViewModelFactory(
    private val repository: HabitRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RemindersViewModel::class.java) ->
                RemindersViewModel(repository) as T

            modelClass.isAssignableFrom(StatsViewModel::class.java) ->
                StatsViewModel(repository) as T

//            modelClass.isAssignableFrom(TodayViewModel::class.java) ->
//                TodayViewModel(repository) as T

            modelClass.isAssignableFrom(NewHabitViewModel::class.java) ->
                NewHabitViewModel(repository) as T

            else -> throw IllegalArgumentException(
                "Unknown ViewModel class: ${modelClass.name}"
            )
        }
    }
}