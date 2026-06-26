package com.gpproject.adhera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.gpproject.adhera.navigation.AdheraNavGraph
import com.gpproject.adhera.ui.theme.AdheraTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // بتخلي الـ UI يمتد تحت شريط الـ Status Bar والـ Navigation Bar
        enableEdgeToEdge()

        setContent {
            AdheraTheme {
                val navController = rememberNavController()
                AdheraNavGraph(navController = navController)
            }
        }
    }
}