package com.example.memorymatrix.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.memorymatrix.game.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel(private val gameStorage: GameStorage) : ViewModel() {

    private val gameEngine = GameEngine()
    private val difficultyEngine = DifficultyEngine()

    private val _difficultyState = MutableStateFlow(
        DifficultyState(level = 1, gridSize = 4, correctCellsCount = 3, displayTimeMs = 1500L, totalRoundsInLevel = 3)
    )
    val difficultyState: StateFlow<DifficultyState> = _difficultyState.asStateFlow()

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private var patternDisplayJob: Job? = null
    private var roundStartTime: Long = 0L

    private var isFirstRoundOfSession: Boolean = true

    init {
    }

    fun checkHasSavedGame(): Boolean = gameStorage.hasSavedGame()

    fun startNewGame() {
        gameStorage.clearSavedGame()
        val defaultDifficulty = DifficultyState(level = 1, gridSize = 4, correctCellsCount = 3, displayTimeMs = 1500L, totalRoundsInLevel = 3)
        _difficultyState.value = defaultDifficulty
        gameStorage.saveGame(defaultDifficulty)
        isFirstRoundOfSession = true
        startNewLevel()
    }

    fun resumeSavedGame() {
        val savedDifficulty = gameStorage.loadGame()
        if (savedDifficulty != null) {
            val calculatedRounds = 3 + ((savedDifficulty.level - 1) / 2)
            _difficultyState.value = savedDifficulty.copy(totalRoundsInLevel = calculatedRounds)
        }
        isFirstRoundOfSession = true
        startNewLevel()
    }

    fun startNewLevel() {
        _gameState.value = GameState(isDisplayingPattern = true, isRoundActive = false)
        startNewRound()
    }

    private fun startNewRound() {
        patternDisplayJob?.cancel()
        val diff = _difficultyState.value

        val (correct, distractors) = gameEngine.generateRoundPattern(
            diff.gridSize,
            diff.correctCellsCount,
            diff.distractorsCount
        )

        _gameState.update {
            it.copy(
                highlightedCells = correct,
                distractorCells = distractors,
                userSelectedCells = emptySet(),
                wrongSelections = emptySet(),
                isDisplayingPattern = true,
                isRoundActive = false
            )
        }

        patternDisplayJob = viewModelScope.launch {
            val actualDisplayTime = if (isFirstRoundOfSession) {
                1500L
            } else {
                diff.displayTimeMs
            }

            delay(actualDisplayTime)

            isFirstRoundOfSession = false

            _gameState.update {
                it.copy(
                    isDisplayingPattern = false,
                    isRoundActive = true
                )
            }
            roundStartTime = System.currentTimeMillis()
        }
    }

    fun onCellClick(row: Int, col: Int, onLevelFinished: (Float) -> Unit) {
        val currentState = _gameState.value
        if (!currentState.isRoundActive || currentState.isDisplayingPattern) return

        val clickedCell = Pair(row, col)
        if (currentState.userSelectedCells.contains(clickedCell) || currentState.wrongSelections.contains(clickedCell)) return

        val maxAllowedClicks = currentState.highlightedCells.size
        if ((currentState.userSelectedCells.size + currentState.wrongSelections.size) >= maxAllowedClicks) return

        if (currentState.highlightedCells.contains(clickedCell)) {
            val updatedSelection = currentState.userSelectedCells + clickedCell
            _gameState.update { it.copy(userSelectedCells = updatedSelection) }
            if ((updatedSelection.size + currentState.wrongSelections.size) >= maxAllowedClicks) {
                endRound(onLevelFinished)
            }
        } else {
            val updatedWrongs = currentState.wrongSelections + clickedCell
            _gameState.update { it.copy(wrongSelections = updatedWrongs) }
            if ((currentState.userSelectedCells.size + updatedWrongs.size) >= maxAllowedClicks) {
                endRound(onLevelFinished)
            }
        }
    }

    private fun endRound(onLevelFinished: (Float) -> Unit) {
        _gameState.update { it.copy(isRoundActive = false) }
        val endTime = System.currentTimeMillis()
        val reactionTime = endTime - roundStartTime

        val currentState = _gameState.value
        val correctCount = currentState.userSelectedCells.size
        val totalClicks = correctCount + currentState.wrongSelections.size
        val roundAccuracy = if (totalClicks > 0) correctCount.toFloat() / totalClicks else 0f

        val updatedAccuracies = currentState.roundAccuracies + roundAccuracy
        val updatedReactionTimes = currentState.reactionTimes + reactionTime

        _gameState.update {
            it.copy(roundAccuracies = updatedAccuracies, reactionTimes = updatedReactionTimes)
        }

        viewModelScope.launch {
            delay(1000L)
            val currentRound = _gameState.value.currentRound
            val totalRounds = _difficultyState.value.totalRoundsInLevel

            if (currentRound < totalRounds) {
                _gameState.update { it.copy(currentRound = currentRound + 1) }
                startNewRound()
            } else {
                val averageAccuracy = updatedAccuracies.average().toFloat()
                val nextDiff = difficultyEngine.calculateNextDifficulty(_difficultyState.value, averageAccuracy)
                _difficultyState.value = nextDiff
                gameStorage.saveGame(nextDiff)
                onLevelFinished(averageAccuracy)
            }
        }
    }
}