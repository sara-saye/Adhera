package com.gpproject.adhera.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.gpproject.adhera.treatment.todo_list.screens.CreateTaskScreen
import com.gpproject.adhera.treatment.todo_list.screens.EditTaskScreen
import com.gpproject.adhera.treatment.todo_list.screens.TaskViewModel
import com.gpproject.adhera.treatment.todo_list.screens.TodoListScreen
import com.gpproject.adhera.ui.screens.treatment.todo_list.TaskDetailsScreen

// ─── Route constants ──────────────────────────────────────────────────────────
object TodoRoutes {
    const val TODO_LIST   = "todo_list"
    const val CREATE_TASK = "create_task"
    const val EDIT_TASK   = "edit_task/{taskId}"
    const val TASK_DETAILS = "task_details/{taskId}"

    fun editTask(taskId: String)    = "edit_task/$taskId"
    fun taskDetails(taskId: String) = "task_details/$taskId"
}

// ─── NavGraph ─────────────────────────────────────────────────────────────────
// غيري السطر ده في TodoNavGraph.kt
@Composable
fun TaskNavGraph(   // ← كانت TodoNavGraph
    navController: NavHostController,
    viewModel: TaskViewModel,
    onBackToHome: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = TodoRoutes.TODO_LIST
    ) {

        // ── القائمة الرئيسية ──────────────────────────────────────────────
        composable(TodoRoutes.TODO_LIST) {
            TodoListScreen(
                onNavigateToCreate  = { navController.navigate(TodoRoutes.CREATE_TASK) },
                onNavigateToEdit    = { id -> navController.navigate(TodoRoutes.editTask(id)) },
                onNavigateToDetails = { id -> navController.navigate(TodoRoutes.taskDetails(id)) },
                onBack              = onBackToHome,
                viewModel           = viewModel
            )
        }

        // ── إنشاء تاسك جديد ──────────────────────────────────────────────
        composable(TodoRoutes.CREATE_TASK) {
            CreateTaskScreen(
                onBack    = { navController.popBackStack() },
                viewModel = viewModel
            )
        }

        // ── تعديل تاسك ───────────────────────────────────────────────────
        composable(
            route = TodoRoutes.EDIT_TASK,
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: return@composable
            EditTaskScreen(
                taskId    = taskId,
                onBack    = { navController.popBackStack() },
                viewModel = viewModel
            )
        }

        // ── تفاصيل تاسك ──────────────────────────────────────────────────
        composable(
            route = TodoRoutes.TASK_DETAILS,
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: return@composable
            TaskDetailsScreen(
                taskId    = taskId,
                viewModel = viewModel,
                onBack    = { navController.popBackStack() },
                onEdit    = { id ->
                    navController.navigate(TodoRoutes.editTask(id))
                }
            )
        }
    }
}