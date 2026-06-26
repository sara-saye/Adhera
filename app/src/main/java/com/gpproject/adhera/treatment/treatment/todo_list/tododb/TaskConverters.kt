package com.gpproject.adhera.treatment.treatment.todo_list.tododb


import androidx.room.TypeConverter

class TaskConverters {

    @TypeConverter
    fun fromMilestonesList(milestones: List<String>): String {
        // بنحول اللستة لنص واحد مفصول بـ "||" كمثال
        return milestones.joinToString(separator = "||")
    }

    @TypeConverter
    fun toMilestonesList(milestonesString: String): List<String> {
        if (milestonesString.isEmpty()) return emptyList()
        // بنرجع نفصل النص تاني عشان نرجعه لستة
        return milestonesString.split("||")
    }
}