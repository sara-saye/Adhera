package com.gpproject.adhera

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.gpproject.adhera.data.local.todo.AppDatabase // تم استيراد الداتابيز الحقيقية بتاعتك
import com.gpproject.adhera.data.remote.ginimiai.GeminiModelsFactory
import com.gpproject.adhera.data.repository.AdheraRepositoryImpl
import com.gpproject.adhera.data.repository.TaskRepositoryImpl // اتأكدي من اسم الكلاس ده عندك
import com.gpproject.adhera.data.usecase.*
import com.gpproject.adhera.ui.navigation.TaskNavGraph
import com.gpproject.adhera.ui.theme.AdheraTheme
import com.gpproject.adhera.viewmodels.AuthViewModel
import com.gpproject.adhera.viewmodels.TaskViewModel
import com.gpproject.adhera.viewmodels.TaskViewModelFactory
import kotlinx.coroutines.launch
import com.gpproject.adhera.data.remote.ginimiai.GeminiRetrofitClient
import com.gpproject.adhera.data.repository.ChatBotRepository
import com.gpproject.adhera.data.repository.ChatBotRepositoryImpl
import com.gpproject.adhera.data.repository.TaskManagerRepository
import com.gpproject.adhera.data.repository.TaskManagerRepositoryImpl

class MainActivity : ComponentActivity() {
    // 1. إنشـاء نسخة من الـ Repository
    private val repository = AdheraRepositoryImpl()
    private val authViewModel: AuthViewModel by viewModels()
    // 1. بنعمل انستنس من الـ Repositories هنا كأننا جوه ViewModel بالظبط 👇
    private val chatBotRepository: ChatBotRepository = ChatBotRepositoryImpl()
    private val taskManagerRepository: TaskManagerRepository = TaskManagerRepositoryImpl()
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





    }



}