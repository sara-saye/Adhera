package com.gpproject.adhera.treatment.habit_tracker.habitdb

import android.content.Context
import com.gpproject.adhera.treatment.habit_tracker.HabitRepository

/**
 * Service Locator خاص بفيتشر الـ Habits.
 *
 * ده بديل لـ Application class مخصصة، لأن فيتشر الـ Habits هنا جزء جوه
 * أبلكيشن أكبر، وده يعني:
 *   - فيه Application class أصلية بالفعل مسجّلة في AndroidManifest.xml خاصة بالمشروع الأكبر
 *   - لازم نتجنب التعارض معاها أو نطلب من حد يعدّل المانيفست
 *
 * الحل: object عادي (Singleton طبيعي في Kotlin) بيتهيّأ بـ Context عادي
 * (Activity context أو Application context أو أي context تاني)، وده كافي لأن
 * AppDatabase.getDatabase() بيستخدم context.applicationContext من جوّه
 * فمش محتاجين Application context بالتحديد.
 *
 * أي Composable في الفيتشر بيستخدم:
 *   HabitServiceLocator.getRepository(context)
 * وهيرجع له نفس instance الـ Repository دايمًا (مش نسخة جديدة كل مرة).
 */
object HabitServiceLocator {

    @Volatile
    private var repositoryInstance: HabitRepository? = null

    /**
     * بيرجع نسخة واحدة بس من الـ Repository طول عمر العملية (Process).
     * أول Context يتم تمريره هو اللي هيُستخدم لبناء الداتابيز.
     */
    fun getRepository(context: Context): HabitRepository {
        return repositoryInstance ?: synchronized(this) {
            repositoryInstance ?: run {
                val database = AppDatabase.getDatabase(context.applicationContext)
                HabitRepository(database.habitDao()).also { repositoryInstance = it }
            }
        }
    }
}