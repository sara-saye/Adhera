package com.gpproject.adhera.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

// ─── Direction ────────────────────────────────────────────────────────────────
enum class LeafDirection {
    UP, DOWN, LEFT, RIGHT;

    fun rotationDegrees(): Float = when (this) {
        LEFT  -> 0f
        UP    -> 90f
        RIGHT -> 180f
        DOWN  -> 270f
    }
}

enum class LeafColor { GREEN, ORANGE }

// ─── Leaf ─────────────────────────────────────────────────────────────────────
data class LeafState(
    val id: Int,
    val facingDirection: LeafDirection,
    val movingDirection: LeafDirection,
    val xFraction: Float,
    val yFraction: Float,
    val color: LeafColor,
    val isDistractor: Boolean = false,
    val alpha: Float = 1f,
)

// ─── Difficulty ───────────────────────────────────────────────────────────────
data class DifficultyParams(
    val tier: Int = 1,
    val leafCount: Int = 8,
    val distractorCount: Int = 0,
    val allowOppositeDirections: Boolean = false,
    val multiplier: Int = 1,
)

// ─── UI State ─────────────────────────────────────────────────────────────────
data class EbbAndFlowUiState(
    val isPlaying: Boolean = false,
    val isPaused: Boolean = false,
    val isGameOver: Boolean = false,

    val gameMode: LeafColor = LeafColor.GREEN,
    val leaves: List<LeafState> = emptyList(),
    val correctDirection: LeafDirection = LeafDirection.UP,

    val score: Int = 0,
    val multiplier: Int = 1,
    val meterFill: Int = 0,
    val timeRemainingMs: Long = 60_000L,
    val streakDots: Int = 0,

    val lastAnswerCorrect: Boolean? = null,
    val showPulseRings: Boolean = false,
    val difficulty: DifficultyParams = DifficultyParams(),
)

// ─── ViewModel ────────────────────────────────────────────────────────────────
class EbbAndFlowViewModel : ViewModel() {

    private val _state = MutableStateFlow(EbbAndFlowUiState())
    val uiState: StateFlow<EbbAndFlowUiState> = _state.asStateFlow()

    private var timerJob:    Job? = null
    private var autoNextJob: Job? = null
    private var pulseJob:    Job? = null

    private var consecCorrect = 0
    private var consecWrong   = 0

    companion object {
        private const val TURN_MS          = 5_000L
        private const val FEEDBACK_MS      =   500L
        private const val PULSE_VISIBLE_MS =   800L
        private const val GAME_MS          = 60_000L
        private const val BASE_POINTS      = 50
        private const val METER_CAP        = 4
        private const val MAX_MULT         = 10
    }

    // الدالة الجديدة للتحكم في زر Continue Game
    fun resumeOrStartNewGame() {
        val s = _state.value
        // إذا كان هناك وقت متبقي، واللعبة لم تنتهِ بعد، وكان هناك نقاط أو أوراق مولدة بالفعل
        if (s.timeRemainingMs > 0 && !s.isGameOver && s.leaves.isNotEmpty()) {
            resumeGame()
        } else {
            startNewGame()
        }
    }

    fun startNewGame() {
        cancelAll()
        consecCorrect = 0
        consecWrong   = 0
        _state.value = EbbAndFlowUiState(isPlaying = true)
        startTimer()
        generateTurn(isFirst = true)
    }

    fun pauseGame() {
        val s = _state.value
        if (!s.isPlaying || s.isPaused) return
        cancelAll()
        _state.update { it.copy(isPaused = true) }
    }

    fun resumeGame() {
        // نغير الحالة لـ playing ونلغي الـ pause
        _state.update { it.copy(isPaused = false, isPlaying = true) }
        startTimer()
        scheduleAutoNext()
    }

    fun onPlayerSwipe(swipeDir: LeafDirection) {
        val s = _state.value
        if (!s.isPlaying || s.isPaused || s.isGameOver) return

        val correct = swipeDir == s.correctDirection

        val newScore: Int
        val newMult:  Int
        val newMeter: Int

        if (correct) {
            consecCorrect++; consecWrong = 0
            newScore = s.score + BASE_POINTS * s.multiplier
            val filled = s.meterFill + 1
            if (filled >= METER_CAP) {
                newMult  = min(MAX_MULT, s.multiplier + 1)
                newMeter = 0
            } else {
                newMult  = s.multiplier
                newMeter = filled
            }
        } else {
            consecWrong++; consecCorrect = 0
            newScore = s.score
            newMeter = 0
            newMult  = if (s.meterFill == 0) max(1, s.multiplier - 1) else s.multiplier
        }

        val newDots = if (correct) min(4, s.streakDots + 1) else 0

        _state.update {
            it.copy(
                score             = newScore,
                multiplier        = newMult,
                meterFill         = newMeter,
                streakDots        = newDots,
                lastAnswerCorrect = correct,
                difficulty        = adjustDifficulty(it.difficulty),
            )
        }

        autoNextJob?.cancel()
        autoNextJob = viewModelScope.launch {
            delay(FEEDBACK_MS)
            _state.update { it.copy(lastAnswerCorrect = null) }
            generateTurn(isFirst = false)
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(100L)
                val s = _state.value
                if (s.isPaused || !s.isPlaying) break
                val rem = max(0L, s.timeRemainingMs - 100L)
                _state.update { it.copy(timeRemainingMs = rem) }
                if (rem == 0L) { endGame(); break }
            }
        }
    }

    private fun endGame() {
        cancelAll()
        _state.update {
            it.copy(
                isPlaying = false,
                isGameOver = true,
                score = it.score + if (it.score > 0) (250 * it.multiplier) else 0
            )
        }
    }

    private fun generateTurn(isFirst: Boolean) {
        val s    = _state.value
        val diff = s.difficulty

        val newMode: LeafColor = if (isFirst) LeafColor.GREEN else {
            if (Random.nextBoolean()) {
                if (s.gameMode == LeafColor.GREEN) LeafColor.ORANGE else LeafColor.GREEN
            } else s.gameMode
        }

        val movingDir: LeafDirection = LeafDirection.values().random()
        val facingDir: LeafDirection = LeafDirection.values().random()

        val correctDir = when (newMode) {
            LeafColor.GREEN  -> facingDir
            LeafColor.ORANGE -> movingDir
        }

        val count = diff.leafCount
        val cols  = 3
        val rows  = (count + cols - 1) / cols
        val mainLeaves = List(count) { i ->
            val col    = i % cols
            val row    = i / cols
            val cellW  = 0.80f / cols
            val cellH  = 0.80f / rows.coerceAtLeast(1)
            val baseX  = 0.10f + col * cellW
            val baseY  = 0.08f + row * cellH
            LeafState(
                id              = i,
                facingDirection = facingDir,
                movingDirection = movingDir,
                xFraction       = baseX + Random.nextFloat() * cellW,
                yFraction       = baseY + Random.nextFloat() * cellH,
                color           = newMode,
                isDistractor    = false,
                alpha           = 1f,
            )
        }

        val distractors = List(diff.distractorCount) { i ->
            LeafState(
                id              = count + i,
                facingDirection = LeafDirection.values().random(),
                movingDirection = LeafDirection.values().random(),
                xFraction       = if (Random.nextBoolean()) Random.nextFloat() * 0.06f
                else 0.94f + Random.nextFloat() * 0.06f,
                yFraction       = Random.nextFloat(),
                color           = newMode,
                isDistractor    = true,
                alpha           = 0.35f,
            )
        }

        flashPulse()

        _state.update {
            it.copy(
                gameMode         = newMode,
                leaves           = mainLeaves + distractors,
                correctDirection = correctDir,
            )
        }

        scheduleAutoNext()
    }

    private fun scheduleAutoNext() {
        autoNextJob?.cancel()
        autoNextJob = viewModelScope.launch {
            delay(TURN_MS)
            val s = _state.value
            if (s.isPlaying && !s.isPaused && !s.isGameOver) generateTurn(isFirst = false)
        }
    }

    private fun flashPulse() {
        pulseJob?.cancel()
        pulseJob = viewModelScope.launch {
            _state.update { it.copy(showPulseRings = true) }
            delay(PULSE_VISIBLE_MS)
            _state.update { it.copy(showPulseRings = false) }
        }
    }

    private fun adjustDifficulty(current: DifficultyParams): DifficultyParams = when {
        consecCorrect > 0 && consecCorrect % 5 == 0 -> buildDiff(min(9, current.tier + 1))
        consecWrong >= 2 -> buildDiff(max(1, current.tier - 1)).also { consecWrong = 0 }
        else -> current
    }

    private fun buildDiff(tier: Int) = when (tier) {
        1    -> DifficultyParams(1,  8,  0, false, 1)
        2    -> DifficultyParams(2,  9,  0, false, 1)
        3    -> DifficultyParams(3, 10,  0, false, 2)
        4    -> DifficultyParams(4, 11,  1, false, 2)
        5    -> DifficultyParams(5, 12,  2, false, 3)
        6    -> DifficultyParams(6, 13,  2, true,  4)
        7    -> DifficultyParams(7, 14,  3, true,  5)
        8    -> DifficultyParams(8, 16,  3, true,  7)
        else -> DifficultyParams(9, 18,  4, true,  9)
    }

    private fun cancelAll() {
        timerJob?.cancel()
        autoNextJob?.cancel()
        pulseJob?.cancel()
    }

    override fun onCleared() { super.onCleared(); cancelAll() }
}