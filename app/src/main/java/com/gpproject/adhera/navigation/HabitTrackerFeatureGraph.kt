package com.gpproject.adhera.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddCircle
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.gpproject.adhera.treatment.habit_tracker.habits.NewHabitScreen
import com.gpproject.adhera.treatment.habit_tracker.reminders.RemindersScreen
import com.gpproject.adhera.treatment.habit_tracker.stats.PerformanceAnalyticsScreen

object HabitTrackerRoutes {
    const val Graph = "habit_tracker_graph"
    const val NewHabit = "habit_tracker/new"
    const val Reminders = "habit_tracker/reminders"
    const val Analytics = "habit_tracker/analytics"
}

private data class HabitTrackerTab(
    val route: String,
    val label: String,
    val icon: ImageVector
)

private val habitTrackerTabs = listOf(
    HabitTrackerTab(
        HabitTrackerRoutes.NewHabit,
        "Add",
        Icons.Outlined.AddCircle
    ),
    HabitTrackerTab(
        HabitTrackerRoutes.Reminders,
        "Reminders",
        Icons.Outlined.Notifications
    ),
    HabitTrackerTab(
        HabitTrackerRoutes.Analytics,
        "Analytics",
        Icons.Outlined.BarChart
    )
)

fun NavGraphBuilder.habitTrackerGraph(
    navController: NavController
) {
    navigation(
        route = HabitTrackerRoutes.Graph,
        startDestination = HabitTrackerRoutes.NewHabit
    ) {

        composable(HabitTrackerRoutes.NewHabit) {
            HabitTrackerSurface(
                selectedRoute = HabitTrackerRoutes.NewHabit,
                navController = navController
            ) {
                NewHabitScreen(
                    onBackClick = {
                        navController.navigate(
                            HabitTrackerRoutes.Reminders
                        ) {
                            launchSingleTop = true

                            popUpTo(HabitTrackerRoutes.Graph) {
                                saveState = true
                            }

                            restoreState = true
                        }
                    }
                )
            }
        }

        composable(HabitTrackerRoutes.Reminders) {
            HabitTrackerSurface(
                selectedRoute = HabitTrackerRoutes.Reminders,
                navController = navController
            ) {
                RemindersScreen()
            }
        }

        composable(HabitTrackerRoutes.Analytics) {
            HabitTrackerSurface(
                selectedRoute = HabitTrackerRoutes.Analytics,
                navController = navController
            ) {
                PerformanceAnalyticsScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HabitTrackerSurface(
    selectedRoute: String,
    navController: NavController,
    content: @Composable () -> Unit
) {
    Scaffold(
        containerColor = HabitTrackerBackground
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 20.dp,
                        vertical = 16.dp
                    )
            ) {

                Text(
                    text = "Habit tracker",
                    style = MaterialTheme.typography.headlineSmall,
                    color = HabitTrackerInk,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Small routines, reviewed without leaving your treatment flow.",
                    color = HabitTrackerMuted,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    habitTrackerTabs.forEach { tab ->

                        FilterChip(
                            selected = selectedRoute == tab.route,

                            onClick = {
                                navController.navigate(tab.route) {

                                    launchSingleTop = true
                                    restoreState = true

                                    popUpTo(
                                        HabitTrackerRoutes.Graph
                                    ) {
                                        saveState = true
                                    }
                                }
                            },

                            label = {
                                Text(tab.label)
                            },

                            leadingIcon = {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = null
                                )
                            }
                        )
                    }
                }
            }

            content()
        }
    }
}

internal val HabitTrackerInk = Color(0xFF102A43)
internal val HabitTrackerAccent = Color(0xFF2E7D8F)
internal val HabitTrackerSoft = Color(0xFFD9EEF2)
internal val HabitTrackerBackground = Color(0xFFF8FAFB)
internal val HabitTrackerCard = Color.White
internal val HabitTrackerMuted = Color(0xFF6B7C86)