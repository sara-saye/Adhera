package com.gpproject.adhera.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gpproject.adhera.treatment.todo_list.screens.CreateTaskScreen
import com.gpproject.adhera.treatment.todo_list.screens.EditTaskScreen
import com.gpproject.adhera.treatment.todo_list.screens.TaskDetailsScreen
import com.gpproject.adhera.treatment.todo_list.screens.TodoListScreen
import com.gpproject.adhera.treatment.todo_list.screens.TaskViewModel

@Composable
fun TaskNavGraph(
    taskViewModel: com.gpproject.adhera.treatment.todo_list.screens.TaskViewModel,
    onBackToHome: () -> Unit // كول باك لو حابة ترجعي للـ Home الأكبر لما اليوزر يضغط باك من شاشة الـ لستة الرئيسية
) {
    val navController = rememberNavController()

    // جعلنا البداية تبدأ مباشرة من شاشة الـ Todo List
    NavHost(navController = navController, startDestination = "todo_list") {

        composable("todo_list") {
            com.gpproject.adhera.treatment.todo_list.screens.TodoListScreen(
                viewModel = taskViewModel,
                onNavigateToCreate = { navController.navigate("create_task") },
                onNavigateToEdit = { id -> navController.navigate("edit_task/$id") },
                onNavigateToDetails = { id -> navController.navigate("task_details/$id") },
                onBack = onBackToHome // يرجعك للـ Hub أو الـ Home الأساسي للتطبيق
            )
        }

        composable("create_task") {
            com.gpproject.adhera.treatment.todo_list.screens.CreateTaskScreen(
                viewModel = taskViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "task_details/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            com.gpproject.adhera.treatment.todo_list.screens.TaskDetailsScreen(
                taskId = taskId,
                viewModel = taskViewModel,
                onBack = { navController.popBackStack() },
                onEdit = { id -> navController.navigate("edit_task/$id") }
            )
        }

        composable(
            route = "edit_task/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            com.gpproject.adhera.treatment.todo_list.screens.EditTaskScreen(
                taskId = taskId,
                viewModel = taskViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}