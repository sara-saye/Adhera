package com.gpproject.adhera.data.local.habit

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.gpproject.adhera.data.model.Habit

@Database(entities = [Habit::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // لو الـ instance جاهز من قبل، استخدمه مباشرة (تجنب الدخول في synchronized كل مرة)
            return INSTANCE ?: synchronized(this) {
                // إعادة الفحص جوه الـ synchronized لمنع إنشاء أكتر من نسخة في حالة التزامن (Thread Safety)
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bloom_habits_db"
                )
                    // ✅ السبب الأساسي للكراش: لو تغيّر شكل الجدول (schema) بدون migration
                    // مكتوبة، Room كان بيرمي IllegalStateException ويكراش التطبيق بالكامل.
                    // الحل هنا: مسح القاعدة القديمة وإعادة إنشائها من جديد بدل الكراش.
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}