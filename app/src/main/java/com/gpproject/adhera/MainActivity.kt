package com.gpproject.adhera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.gpproject.adhera.data.local.todo.AppDatabase // تم استيراد الداتابيز الحقيقية بتاعتك
import com.gpproject.adhera.data.repository.TaskRepositoryImpl // اتأكدي من اسم الكلاس ده عندك
import com.gpproject.adhera.data.usecase.*
import com.gpproject.adhera.ui.navigation.TaskNavGraph
import com.gpproject.adhera.ui.theme.AdheraTheme
import com.gpproject.adhera.viewmodels.AuthViewModel
import com.gpproject.adhera.viewmodels.TaskViewModel
import com.gpproject.adhera.viewmodels.TaskViewModelFactory

class MainActivity : ComponentActivity() {

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
                // استدعاء الـ Navigation Graph المستقل وخلاص كدة الـ Flow جاهز للعمل!
                TaskNavGraph(
                    taskViewModel = taskViewModel,
                    onBackToHome = {
                        finish() // يرجعك للـ Hub الأساسي للتطبيق
                    }
                )
            }
        }
    }
}