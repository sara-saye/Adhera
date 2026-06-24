package com.gpproject.adhera.ui.screens.treatment.games.colormatchgame

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ColorMatchViewModel : ViewModel() {

    var currentScreen by mutableStateOf<ColorMatchScreenState>(ColorMatchScreenState.Home)
        private set

    var bestScore by mutableStateOf(0)
        private set

    // Game states
    var timeLeft by mutableStateOf(45)
        private set
    var score by mutableStateOf(0)
        private set
    var multiplier by mutableStateOf(1)
        private set
    var streak by mutableStateOf(0)
        private set
    var round by mutableStateOf(generateRound())
        private set
    var countdown by mutableStateOf(3)
        private set
    var phase by mutableStateOf("countdown") // "countdown", "playing", "feedback"
        private set
    var feedbackOk by mutableStateOf(true)
        private set
    var showPause by mutableStateOf(false)
        private set
    var isPaused by mutableStateOf(false)
        private set

    private var roundSpeed = 800L
    private var timerJob: Job? = null

    fun navigateTo(screen: ColorMatchScreenState) {
        currentScreen = screen
        if (screen is ColorMatchScreenState.Game) {
            resetGame()
        }
    }

    private fun resetGame() {
        timeLeft = 45
        score = 0
        multiplier = 1
        streak = 0
        roundSpeed = 800L
        countdown = 3
        phase = "countdown"
        round = generateRound()
        isPaused = false
        showPause = false

        startCountdown()
    }

    private fun startCountdown() {
        viewModelScope.launch {
            delay(600)
            countdown = 2
            delay(600)
            countdown = 1
            delay(600)
            phase = "playing"
            startTimer()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (timeLeft > 0) {
                delay(1000)
                if (!isPaused && phase == "playing") {
                    timeLeft--
                }
            }
            if (timeLeft <= 0) {
                if (score > bestScore) bestScore = score
                currentScreen = ColorMatchScreenState.GameOver(score, bestScore)
            }
        }
    }

    fun handleAnswer(userSaysYes: Boolean, onAnimateMultiplier: suspend () -> Unit) {
        if (phase != "playing" || isPaused) return

        viewModelScope.launch {
            val correct = userSaysYes == round.answer
            feedbackOk = correct
            phase = "feedback"

            if (correct) {
                streak++
                if (streak % 4 == 0 && multiplier < 10) {
                    multiplier++
                    onAnimateMultiplier()
                }
                score += 100 * multiplier
                roundSpeed = maxOf(300L, roundSpeed - 30L)
            } else {
                streak = 0
                if (multiplier > 1) multiplier--
                roundSpeed = 800L
            }

            delay(roundSpeed)
            if (timeLeft > 0) {
                round = generateRound()
                phase = "playing"
            }
        }
    }

    fun pauseGame() {
        isPaused = true
        showPause = true
    }

    fun resumeGame() {
        isPaused = false
        showPause = false
    }

    fun restartGameFromPause() {
        resetGame()
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}