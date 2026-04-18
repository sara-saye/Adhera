package com.gpproject.adhera.ui.theme


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable

private val AdheraColorScheme = lightColorScheme(

    primary = NavyPrimary,
    secondary = NavySecondary,

    background = AppBackground,
    surface = CardBackground,

    onPrimary = CardBackground,
    onSecondary = CardBackground,

    onBackground = TextPrimary,
    onSurface = TextPrimary
)

@Composable
fun AdheraTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    MaterialTheme(
        colorScheme = AdheraColorScheme,
        typography = Typography(),
        shapes = Shapes(),
        content = content
    )
}