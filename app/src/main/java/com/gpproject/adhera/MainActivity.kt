package com.gpproject.adhera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.gpproject.adhera.ui.navigation.ChatBotRoute
import com.gpproject.adhera.ui.navigation.chatBotScreen

class MainActivity : ComponentActivity() {

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContent {

            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = ChatBotRoute.ROUTE
            ) {

                chatBotScreen(
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}