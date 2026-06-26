package com.gpproject.adhera.treatment.games.colormatchgame

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gpproject.adhera.treatment.games.colormatchgame.ColorMatchConstants.WoodBrown

@Composable
fun ColorMatchGameScreen(
    onExitGame: () -> Unit,
    viewModel: ColorMatchViewModel = viewModel()
) {
    Box(modifier = Modifier.fillMaxSize().background(WoodBrown)) {
        WoodGrainOverlay()

        when (val state = viewModel.currentScreen) {
            is ColorMatchScreenState.Home -> {
                HomeScreen(
                    onPlay = { viewModel.navigateTo(ColorMatchScreenState.Game) },
                    onHowToPlay = { viewModel.navigateTo(ColorMatchScreenState.HowToPlay) }
                )
            }
            is ColorMatchScreenState.HowToPlay -> {
                HowToPlayScreen(
                    onBack = { viewModel.navigateTo(ColorMatchScreenState.Home) },
                    onStart = { viewModel.navigateTo(ColorMatchScreenState.Game) }
                )
            }
            is ColorMatchScreenState.Game -> {
                GameplayScreen(
                    timeLeft = viewModel.timeLeft,
                    score = viewModel.score,
                    streak = viewModel.streak,
                    multiplier = viewModel.multiplier,
                    round = viewModel.round,
                    countdown = viewModel.countdown,
                    phase = viewModel.phase,
                    feedbackOk = viewModel.feedbackOk,
                    showPause = viewModel.showPause,
                    onAnswer = { answer, anim -> viewModel.handleAnswer(answer, anim) },
                    onPauseClick = { viewModel.pauseGame() },
                    onPauseContinue = { viewModel.resumeGame() },
                    onPauseRestart = { viewModel.restartGameFromPause() },
                    onPauseExit = onExitGame
                )
            }
            is ColorMatchScreenState.GameOver -> {
                GameOverScreen(
                    score = state.score,
                    best = state.best,
                    onPlayAgain = { viewModel.navigateTo(ColorMatchScreenState.Game) },
                    onHome = { viewModel.navigateTo(ColorMatchScreenState.Home) }
                )
            }
        }
    }
}