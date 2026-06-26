package com.gpproject.adhera.treatment.habit_tracker.habits
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gpproject.adhera.treatment.habit_tracker.Habit
import com.gpproject.adhera.treatment.habit_tracker.HabitRepository
import kotlinx.coroutines.launch

class NewHabitViewModel(private val repository: HabitRepository) : ViewModel() {

    // States لتسجيل مدخلات المستخدم من الواجهة
    var habitName by mutableStateOf("")
    var selectedCategory by mutableStateOf("Mind")
    var selectedColorHex by mutableStateOf("#032B43") // اللون الافتراضي الداكن
    var reminderTime by mutableStateOf("08:00 AM")
    var selectedDays by mutableStateOf(setOf("M", "T", "W", "T", "F")) // الافتراضي أيام العمل كما بالصورة

    // دالة حفظ العادة في قاعدة البيانات
    fun saveHabit(onSuccess: () -> Unit) {
        if (habitName.isBlank()) return

        viewModelScope.launch {
            val daysString = selectedDays.joinToString(",")
            val newHabit = Habit(
                name = habitName,
                category = selectedCategory,
                colorHex = selectedColorHex,
                reminderTime = reminderTime,
                repeatDays = daysString
            )
            repository.insert(newHabit)
            onSuccess() // العودة للشاشة السابقة بعد النجاح
        }
    }

    // دالة لتعديل الأيام المختارة (إضافة أو إزالة)
    fun toggleDay(day: String) {
        selectedDays = if (selectedDays.contains(day)) {
            selectedDays - day
        } else {
            selectedDays + day
        }
    }
}