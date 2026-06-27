package com.gpproject.adhera.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.gpproject.adhera.doctor.data.DoctorViewModel
import com.gpproject.adhera.doctor.ui.DoctorHomeScreen

object DoctorRoutes {
    const val GRAPH = "doctor_graph"
    const val HOME = "doctor_home"
}

fun NavGraphBuilder.doctorNavGraph(
    navController: NavHostController,
    doctorViewModel: DoctorViewModel,
    onOpenFullDetection: () -> Unit,
    onOpenEeg: () -> Unit,
    onOpenMri: () -> Unit,
    onOpenFocusTest: () -> Unit,
    onOpenAssessment: () -> Unit,
) {
    navigation(
        startDestination = DoctorRoutes.HOME,
        route = DoctorRoutes.GRAPH,
    ) {
        composable(DoctorRoutes.HOME) {
            DoctorHomeScreen(
                doctorViewModel = doctorViewModel,
                onOpenFullDetection = onOpenFullDetection,
                onOpenEeg = onOpenEeg,
                onOpenMri = onOpenMri,
                onOpenFocusTest = onOpenFocusTest,
                onOpenAssessment = onOpenAssessment,
                onOpenTodo = { navController.navigate(Routes.TODO) },
                onOpenHabitTracker = { navController.navigate(Routes.HABIT_TRACKER) },
                onOpenChatbot = { navController.navigate(Routes.CHATBOT) },
                onOpenFocusGames = { navController.navigate(Routes.FOCUS_GAMES_MENU) },
            )
        }
    }
}
