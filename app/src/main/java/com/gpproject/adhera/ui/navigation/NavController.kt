//package com.gpproject.adhera.ui.navigation
//
//import androidx.compose.runtime.*
//import androidx.navigation.NavType
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import androidx.navigation.navArgument
//import com.gpproject.adhera.ui.screens.detection.*
//import com.gpproject.adhera.ui.screens.home.HomeHubScreen
//import com.gpproject.adhera.ui.screens.onboarding.*
//import com.gpproject.adhera.ui.screens.auth.*
//import com.gpproject.adhera.ui.screens.*
//import com.gpproject.adhera.ui.screens.reports.DetectionCompleteScreen
//import com.gpproject.adhera.ui.screens.reports.DetectionResultsScreen
//import com.gpproject.adhera.ui.screens.splash.AdheraAnimatedSplash
//import com.gpproject.adhera.R
//
//import com.gpproject.adhera.ui.screens.treatment.games.EbbAndFlowScreen
//import com.gpproject.adhera.ui.screens.treatment.games.FocusGamesMenuScreen
//import com.gpproject.adhera.ui.screens.treatment.games.EbbAndFlowIntroScreen
//import com.gpproject.adhera.ui.screens.treatment.games.EbbAndFlowHowToPlayScreen
//import com.gpproject.adhera.ui.screens.treatment.todo_list.*
//import com.gpproject.adhera.viewmodels.EbbAndFlowViewModel
//import com.gpproject.adhera.viewmodels.TaskViewModel
//import androidx.lifecycle.viewmodel.compose.viewModel
//
//@Composable
//fun AdheraNavGraph(taskViewModel: TaskViewModel) {
//    val navController = rememberNavController()
//
//    NavHost(
//        navController = navController,
//        startDestination = "home_hub"
//    ) {
//
//        // ==================== Focus Games Flow ====================
//
//        // الشاشة الأولى — قائمة الألعاب
//        composable("focus_games_menu") {
//            FocusGamesMenuScreen(
//                onBack             = { navController.popBackStack() },
//                onSettingsClick    = { /* TODO: navigate to settings */ },
//                onPlayEbbAndFlow   = { navController.navigate("ebb_and_flow_intro") },
//                onPlayMemoryMatrix = { /* TODO */ },
//                onPlayColorMatch   = { /* TODO */ },
//            )
//        }
//
//        // الشاشة التانية — intro اللعبة
//        composable("ebb_and_flow_intro") {
//            EbbAndFlowIntroScreen(
//                onBack         = { navController.popBackStack() },
//                onSettings     = { /* TODO */ },
//                onNewGame      = { navController.navigate("ebb_and_flow_game") },
//                onHowToPlay    = { navController.navigate("ebb_and_flow_how_to_play") },
//            )
//        }
//
//        // الشاشة التالتة — how to play
//        composable("ebb_and_flow_how_to_play") {
//            EbbAndFlowHowToPlayScreen(
//                onBack      = { navController.popBackStack() },
//                onSettings  = { /* TODO */ },
//                onStartGame = {
//                    navController.navigate("ebb_and_flow_game") {
//                        popUpTo("ebb_and_flow_how_to_play") { inclusive = true }
//                    }
//                },
//            )
//        }
//
//        // الشاشة الرابعة — اللعبة نفسها
//        composable("ebb_and_flow_game") {
//            val vm: EbbAndFlowViewModel = viewModel()
//            EbbAndFlowScreen(
//                onBack = { navController.popBackStack() },
//                vm     = vm,
//            )
//        }
//
//        // ==================== Home Hub ====================
//        composable("home_hub") {
//            HomeHubScreen(
//                onNavigateToTodo       = { navController.navigate("todo_list") },
//                onNavigateToFocusGames = { navController.navigate("focus_games_menu") } // ← عدلناها
//            )
//        }
//
//        // ==================== To-Do List Flow ====================
//        composable("todo_list") {
//            TodoListScreen(
//                viewModel          = taskViewModel,
//                onNavigateToCreate  = { navController.navigate("create_task") },
//                onNavigateToEdit    = { id -> navController.navigate("edit_task/$id") },
//                onNavigateToDetails = { id -> navController.navigate("task_details/$id") },
//                onBack              = { navController.popBackStack() }
//            )
//        }
//
//        composable("create_task") {
//            CreateTaskScreen(viewModel = taskViewModel, onBack = { navController.popBackStack() })
//        }
//
//        composable(
//            route     = "task_details/{taskId}",
//            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
//        ) { backStackEntry ->
//            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
//            TaskDetailsScreen(
//                taskId    = taskId,
//                viewModel = taskViewModel,
//                onBack    = { navController.popBackStack() },
//                onEdit    = { id -> navController.navigate("edit_task/$id") }
//            )
//        }
//
//        composable(
//            route     = "edit_task/{taskId}",
//            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
//        ) { backStackEntry ->
//            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
//            EditTaskScreen(taskId = taskId, viewModel = taskViewModel, onBack = { navController.popBackStack() })
//        }
//
//        // ==================== Onboarding ====================
//        composable("onboarding1") {
//            OnboardingScreen1(
//                onContinue = { navController.navigate("onboarding3") },
//                onSkip     = { navController.navigate("role_selection") }
//            )
//        }
//
//        composable("onboarding3") {
//            OnboardingScreen3(
//                onStart = { navController.navigate("role_selection") },
//                onSkip  = { navController.navigate("role_selection") }
//            )
//        }
//
//        // ==================== Role Selection ====================
//        composable("role_selection") {
//            RoleSelectionScreen(
//                onRoleSelected = { navController.navigate("detection_welcome") }
//            )
//        }
//
//        // ==================== Detection Flow ====================
//        composable("detection_welcome") {
//            ADHDDetectionWelcomeScreen(
//                onStartDetection = { navController.navigate("camera_permission") }
//            )
//        }
//
//        composable("camera_permission") {
//            CameraPermissionScreen(
//                onPermissionGranted = { navController.navigate("upload_data") },
//                onSecondaryAction   = { navController.popBackStack() }
//            )
//        }
//
//        composable("upload_data") {
//            MedicalUploadScreen(
//                onNext = { navController.navigate("assessment_intro") },
//                onSkip = { navController.navigate("assessment_intro") },
//                onBack = { navController.popBackStack() }
//            )
//        }
//
//        composable("assessment_intro") {
//            AssessmentIntroScreen(
//                stageIndex = 2,
//                onBack     = { navController.popBackStack() },
//                onReady    = { navController.navigate("assessment") }
//            )
//        }
//
//        // ==================== Assessment ====================
//        composable("assessment") {
//            var currentQuestion by remember { mutableStateOf(0) }
//            AssessmentScreen(
//                stageIndex    = 2,
//                questionIndex = currentQuestion,
//                onNext = {
//                    if (currentQuestion < 9) currentQuestion++
//                    else navController.navigate("focus_test_intro")
//                },
//                onBack = {
//                    if (currentQuestion > 0) currentQuestion--
//                    else navController.popBackStack()
//                }
//            )
//        }
//
//        // ==================== Focus Test Flow ====================
//        composable("focus_test_intro") {
//            FocusTestIntroScreen(
//                stageIndex = 3,
//                onBack     = { navController.popBackStack() },
//                onReady    = { navController.navigate("observation_flow") }
//            )
//        }
//
//        composable("observation_flow") {
//            SynapticFlowObservationScreen(
//                stageIndex      = 3,
//                onBack          = { navController.popBackStack() },
//                onFlowComplete  = { navController.navigate("recognition_test") }
//            )
//        }
//
//        composable("recognition_test") {
//            SynapticFlowTestScreen(
//                stageIndex = 3,
//                testImage  = R.drawable.photo_3,
//                onBack     = { navController.popBackStack() },
//                onAnswer   = { navController.navigate("detection_complete") }
//            )
//        }
//
//        // ==================== Results ====================
//        composable("detection_complete") {
//            DetectionCompleteScreen(
//                onViewReport = { navController.navigate("detection_results") }
//            )
//        }
//
//        composable("detection_results") {
//            DetectionResultsScreen(
//                onDone = { navController.navigate("sign_up") }
//            )
//        }
//
//        // ==================== Auth ====================
//        composable("sign_up") {
//            SignUpScreen(
//                role            = "Patient",
//                onSignUpComplete = { navController.navigate("registration_success") }
//            )
//        }
//
//        composable("registration_success") {
//            RegistrationSuccessScreen(
//                onContinue = {
//                    navController.navigate("home_hub") {
//                        popUpTo("onboarding1") { inclusive = true }
//                    }
//                }
//            )
//        }
//    }
//}


package com.gpproject.adhera.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

import com.gpproject.adhera.ui.screens.home.HomeHubScreen
import com.gpproject.adhera.ui.screens.treatment.habit_tracker.habits.NewHabitScreen
import com.gpproject.adhera.ui.screens.treatment.habit_tracker.reminders.RemindersScreen
import com.gpproject.adhera.ui.screens.treatment.habit_tracker.stats.PerformanceAnalyticsScreen

// ================= Bottom Navigation =================

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {

    object Home : BottomNavItem(
        route = "home_hub",
        title = "Today",
        icon = Icons.Outlined.Home
    )

    object Analytics : BottomNavItem(
        route = "performance_analytics",
        title = "Habits",
        icon = Icons.Outlined.BarChart
    )

    object Reminders : BottomNavItem(
        route = "reminders_screen",
        title = "Reminders",
        icon = Icons.Outlined.Notifications
    )

    object Profile : BottomNavItem(
        route = "profile_screen",
        title = "Profile",
        icon = Icons.Outlined.Person
    )
}

@Composable
fun BottomBar(navController: NavController) {

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Analytics,
        BottomNavItem.Reminders,
        BottomNavItem.Profile
    )

    val navBackStackEntry =
        navController.currentBackStackEntryAsState().value

    val currentRoute =
        navBackStackEntry?.destination?.route

    NavigationBar {

        items.forEach { item ->

            NavigationBarItem(
                selected = currentRoute == item.route,

                onClick = {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                    }
                },

                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.title
                    )
                },

                label = {
                    Text(item.title)
                }
            )
        }
    }
}

// ================= Nav Graph =================

@Composable
fun AdheraNavGraph() {

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomBar(navController)
        }
    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = "home_hub",
            modifier = Modifier.padding(paddingValues)
        ) {

            // ================= Home =================

//            composable("home_hub") {
//                HomeHubScreen(
//                    onNavigateToHabits = {
//                        navController.navigate("new_habit_route")
//                    }
//                )
//            }

            // ================= Analytics =================

            composable("performance_analytics") {
                PerformanceAnalyticsScreen()
            }

            // ================= Reminders =================

            composable("reminders_screen") {
                RemindersScreen()
            }

            // ================= New Habit =================

            composable("new_habit_route") {
                NewHabitScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }

            // ================= Dummy Profile =================

            composable("profile_screen") {
                Text("Profile Screen")
            }
        }
    }
}