package com.gpproject.adhera.ui.components


import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier


@Composable
fun AnimatedEntrance(
    visible: Boolean = true,
    delayMillis: Int = 0,
    durationMillis: Int = 600,
    slideDistance: Int = 40,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(
            animationSpec = tween(
                durationMillis = durationMillis,
                delayMillis = delayMillis,
                easing = FastOutSlowInEasing
            )
        ) + slideInVertically(
            animationSpec = tween(
                durationMillis = durationMillis,
                delayMillis = delayMillis,
                easing = FastOutSlowInEasing
            ),
            initialOffsetY = { it / slideDistance }
        ),
        exit = fadeOut() + slideOutVertically()
    ) {
        content()
    }
}