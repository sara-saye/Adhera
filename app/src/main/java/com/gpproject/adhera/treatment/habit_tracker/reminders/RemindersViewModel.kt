package com.gpproject.adhera.treatment.habit_tracker.reminders

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gpproject.adhera.treatment.habit_tracker.HabitRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class RemindersViewModel(private val repository: HabitRepository) : ViewModel() {

    // جلب العادات النشطة لمعرفة عدد العادات المفعل لها تنبيهات (6 Active Habits مثلاً)
    val activeHabitsCount: StateFlow<Int> = repository.allHabits
        .map { habits -> habits.size }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 6
        )

    // States للتحكم في الأزرار (Switches) الخاصة بكل كارد بالتصميم
    var isHydrationEnabled by mutableStateOf(true)
    var isMindfulMinutesEnabled by mutableStateOf(true)
    var isDeepWorkEnabled by mutableStateOf(false)
    var isReadingNotificationEnabled by mutableStateOf(true)

    // إعدادات كارد التقرير اليومي (Daily Summary)
    var isMorningSummary by mutableStateOf(true) // True = Morning, False = Evening
    var summaryTime by mutableStateOf("07:30 AM")

    // قائمة فترات شرب الماء الافتراضية كما بالصورة
    var hydrationSlots by mutableStateOf(listOf("09:00 AM", "11:00 AM", "01:00 PM"))

    fun addHydrationSlot(time: String) {
        hydrationSlots = hydrationSlots + time
    }

    fun toggleHydration(enabled: Boolean) {
        isHydrationEnabled = enabled
    }
}