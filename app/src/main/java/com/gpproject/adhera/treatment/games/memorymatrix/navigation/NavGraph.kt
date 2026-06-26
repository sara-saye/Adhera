package com.example.memorymatrix.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.memorymatrix.ui.screens.GameScreen
import com.example.memorymatrix.ui.screens.InstructionScreen
import com.example.memorymatrix.ui.screens.SessionSummaryScreen
import com.example.memorymatrix.ui.screens.StartScreen
import com.example.memorymatrix.viewmodel.GameViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: GameViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "start_screen"
    ) {
        composable("start_screen") {
            StartScreen(
                hasSavedGame = viewModel.checkHasSavedGame(),
                onNewGameClick = {
                    viewModel.startNewGame()
                    navController.navigate("game_screen")
                },
                onResumeGameClick = {
                    viewModel.resumeSavedGame()
                    navController.navigate("game_screen")
                },
                onInstructionsClick = {
                    navController.navigate("instruction_screen")
                }
            )
        }

        composable("game_screen") {
            GameScreen(
                viewModel = viewModel,
                onLevelFinished = { averageAccuracy ->
                    val avgTime = viewModel.gameState.value.reactionTimes.average().toLong()

                    navController.navigate("report_screen/$averageAccuracy/$avgTime") {
                        popUpTo("start_screen") { inclusive = false }
                    }
                }
            )
        }

        composable(
            route = "report_screen/{accuracy}/{reactionTime}",
            arguments = listOf(
                navArgument("accuracy") { type = NavType.FloatType },
                navArgument("reactionTime") { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val accuracy = backStackEntry.arguments?.getFloat("accuracy") ?: 0f
            val reactionTime = backStackEntry.arguments?.getLong("reactionTime") ?: 0L

            SessionSummaryScreen(
                accuracy = accuracy,
                avgReactionTime = reactionTime,
                onHomeClick = {
                    navController.popBackStack("start_screen", false)
                }
            )
        }

        composable("instruction_screen") {
            InstructionScreen(
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
    }
}