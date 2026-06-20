package com.gpproject.adhera.ui.navigation
//
//
//import androidx.compose.runtime.*
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import com.gpproject.adhera.ui.screens.detection.*
//import com.gpproject.adhera.ui.screens.home.HomeHubScreen
//import com.gpproject.adhera.ui.screens.onboarding.*
//import com.gpproject.adhera.ui.screens.auth.*
//import com.gpproject.adhera.ui.screens.*   // لو عايزة تستوردي كل حاجة (اختياري)
//import com.gpproject.adhera.ui.screens.reports.DetectionCompleteScreen
//import com.gpproject.adhera.ui.screens.reports.DetectionResultsScreen
//import com.gpproject.adhera.ui.screens.splash.AdheraAnimatedSplash
//import com.gpproject.adhera.R
//@Composable
//fun AdheraNavGraph() {
//    val navController = rememberNavController()
//
//    // Splash Screen
//    var showSplash by remember { mutableStateOf(true) }
//
//    if (showSplash) {
//        AdheraAnimatedSplash {
//            showSplash = false
//        }
//    } else {
//        NavHost(
//            navController = navController,
//            startDestination = "onboarding1"
//        ) {
//            // ==================== Onboarding ====================
//            composable("onboarding1") {
//                OnboardingScreen1(
//                    onContinue = { navController.navigate("onboarding3") },
//                    onSkip = { navController.navigate("role_selection") }
//                )
//            }
//
//            composable("onboarding3") {
//                OnboardingScreen3(
//                    onStart = { navController.navigate("role_selection") },
//                    onSkip = { navController.navigate("role_selection") }
//                )
//            }
//
//            // ==================== Role Selection ====================
//            composable("role_selection") {
//                RoleSelectionScreen(
//                    onRoleSelected = { navController.navigate("detection_welcome") }
//                )
//            }
//
//            // ==================== Detection Flow ====================
//            composable("detection_welcome") {
//                ADHDDetectionWelcomeScreen(
//                    onStartDetection = { navController.navigate("camera_permission") }
//                )
//            }
//
//            composable("camera_permission") {
//                CameraPermissionScreen(
//                    onPermissionGranted = { navController.navigate("upload_data") },
//                    onSecondaryAction = { navController.popBackStack() }
//                )
//            }
//
//            composable("upload_data") {
//                MedicalUploadScreen(
//                    onNext = { navController.navigate("assessment_intro") },
//                    onSkip = { navController.navigate("assessment_intro") },
//                    onBack = { navController.popBackStack() }
//                )
//            }
//
//            composable("assessment_intro") {
//                AssessmentIntroScreen(
//                    stageIndex = 2,
//                    onBack = { navController.popBackStack() },
//                    onReady = { navController.navigate("assessment") }
//                )
//            }
//
//            // ==================== Assessment (Personality) ====================
//            composable("assessment") {
//                var currentQuestion by remember { mutableStateOf(0) }
//
//                AssessmentScreen(
//                    stageIndex = 2,
//                    questionIndex = currentQuestion,
//                    onNext = {
//                        if (currentQuestion < 9) {
//                            currentQuestion++
//                        } else {
//                            navController.navigate("focus_test_intro")
//                        }
//                    },
//                    onBack = {
//                        if (currentQuestion > 0) {
//                            currentQuestion--
//                        } else {
//                            navController.popBackStack()
//                        }
//                    }
//                )
//            }
//
//            // ==================== Focus Test Flow ====================
//            composable("focus_test_intro") {
//                FocusTestIntroScreen(
//                    stageIndex = 3,
//                    onBack = { navController.popBackStack() },
//                    onReady = { navController.navigate("observation_flow") }
//                )
//            }
//
//            composable("observation_flow") {
//                SynapticFlowObservationScreen(
//                    stageIndex = 3,
//                    onBack = { navController.popBackStack() },
//                    onFlowComplete = { navController.navigate("recognition_test") }
//                )
//            }
//
//            composable("recognition_test") {
//                SynapticFlowTestScreen(
//                    stageIndex = 3,
//                    testImage = R.drawable.photo_3,
//                    onBack = { navController.popBackStack() },
//                    onAnswer = { navController.navigate("detection_complete") }
//                )
//            }
//
//            // ==================== Results ====================
//            composable("detection_complete") {
//                DetectionCompleteScreen(
//                    onViewReport = { navController.navigate("detection_results") }
//                )
//            }
//
//            composable("detection_results") {
//                DetectionResultsScreen(
//                    onDone = { navController.navigate("sign_up") }
//                )
//            }
//
//            // ==================== Auth ====================
//            composable("sign_up") {
//                SignUpScreen(
//                    onSignUpComplete = { navController.navigate("registration_success") }
//                )
//            }
//
//            composable("registration_success") {
//                RegistrationSuccessScreen(
//                    onContinue = {
//                        navController.navigate("home_hub") {
//                            popUpTo("onboarding1") { inclusive = true }
//                        }
//                    }
//                )
//            }
//
//            composable("home_hub") {
//                HomeHubScreen()
//            }
//        }
//    }
//}


//nourhan code 33333333333333333333333333333


import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gpproject.adhera.ui.screens.home.HomeHubScreen
import com.gpproject.adhera.ui.screens.treatment.todo_list.*
import com.gpproject.adhera.viewmodels.TaskViewModel

@Composable
fun AdheraNavGraph(taskViewModel: TaskViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home_hub") {
        composable("home_hub") {
            HomeHubScreen(onNavigateToTodo = { navController.navigate("todo_list") })
        }

        composable("todo_list") {
            TodoListScreen(
                viewModel = taskViewModel,
                onNavigateToCreate = { navController.navigate("create_task") },
                onNavigateToEdit = { id -> navController.navigate("edit_task/$id") },
                onNavigateToDetails = { id -> navController.navigate("task_details/$id") },
                onBack = { navController.popBackStack() }
            )
        }

        composable("create_task") {
            CreateTaskScreen(viewModel = taskViewModel, onBack = { navController.popBackStack() })
        }

        composable(
            route = "task_details/{taskId}",
            arguments = listOf(navArgument("taskId") { type = NavType.StringType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
            TaskDetailsScreen(
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
            EditTaskScreen(taskId = taskId, viewModel = taskViewModel, onBack = { navController.popBackStack() })
        }
    }
}