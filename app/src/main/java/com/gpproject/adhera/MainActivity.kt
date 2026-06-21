package com.gpproject.adhera

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.gpproject.adhera.data.local.todo.AppDatabase // تم استيراد الداتابيز الحقيقية بتاعتك
import com.gpproject.adhera.data.repository.AdheraRepositoryImpl
import com.gpproject.adhera.data.repository.TaskRepositoryImpl // اتأكدي من اسم الكلاس ده عندك
import com.gpproject.adhera.data.usecase.*
import com.gpproject.adhera.ui.navigation.TaskNavGraph
import com.gpproject.adhera.ui.theme.AdheraTheme
import com.gpproject.adhera.viewmodels.AuthViewModel
import com.gpproject.adhera.viewmodels.TaskViewModel
import com.gpproject.adhera.viewmodels.TaskViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    // 1. إنشـاء نسخة من الـ Repository
    private val repository = AdheraRepositoryImpl()
    private val authViewModel: AuthViewModel by viewModels()

    // بناء الـ TaskViewModel يدوياً بـ استخدام الفاكتوري والـ AppDatabase الحقيقية بتاعتك
    private val taskViewModel: TaskViewModel by viewModels {
        // 1. جلب الـ Dao من الـ AppDatabase الحقيقية
        val database = AppDatabase.getDatabase(applicationContext)
        val taskDao = database.taskDao

        // 2. عمل الـ Repository اللي بيكلم الـ Dao
        val repository = TaskRepositoryImpl(taskDao)

        // 3. تجميع الـ UseCases اللي الـ ViewModel محتاجها
        val taskUseCases = TaskUseCases(
            getAllTasks = GetAllTasksUseCase(repository),
            getTaskById = GetTaskByIdUseCase(repository),
            upsertTask = UpsertTaskUseCase(repository),
            deleteTask = DeleteTaskUseCase(repository)
        )

        // 4. تمرير الـ UseCases للـ Factory
        TaskViewModelFactory(taskUseCases)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

      setContent {
            AdheraTheme {
//                // استدعاء الـ Navigation Graph المستقل وخلاص كدة الـ Flow جاهز للعمل!
//                TaskNavGraph(
//                    taskViewModel = taskViewModel,
//                    onBackToHome = {
//                        finish() // يرجعك للـ Hub الأساسي للتطبيق
//                    }
//                )
            }
        }
// 2. تشغيل التست في الـ Coroutine Scope
        lifecycleScope.launch {
            testAdheraServer()
        }




    }

    private suspend fun testAdheraServer() {
        try {
            Log.d("AdheraTest", "=== بدء فحص السيرفر ===")

            // التست الأول: فحص الـ Health Check
            val healthResponse = repository.checkHealth()
            if (healthResponse.isSuccessful && healthResponse.body() != null) {
                val status = healthResponse.body()
                Log.d("AdheraTest", "✅ السيرفر شغال! حالة الموديلات:")
                Log.d("AdheraTest", "MRI Model: ${status?.mriModel}")
                Log.d("AdheraTest", "EEG Model: ${status?.eegModel}")
            } else {
                Log.e("AdheraTest", "❌ فشل في الاتصال بالسيرفر: ${healthResponse.errorBody()?.string()}")
            }

            // التست الثاني: تجربة Questionnaire
            val dummyFeatures = listOf(1.0, 2.0, 3.0, 4.0, 5.0, 6.0, 7.0, 8.0, 9.0, 10.0)
            val qResponse = repository.predictQuestionnaire(dummyFeatures)
            if (qResponse.isSuccessful) {
                Log.d("AdheraTest", "✅ تيسيت الـ Questionnaire نجح! النتيجة: ${qResponse.body()?.prediction}")
            } else {
                Log.e("AdheraTest", "❌ فشل تست الـ Questionnaire: ${qResponse.errorBody()?.string()}")
            }

        } catch (e: Exception) {
            Log.e("AdheraTest", "💥 حصلت مشكلة أثناء الاتصال: ${e.localizedMessage}")
        }
    }
}