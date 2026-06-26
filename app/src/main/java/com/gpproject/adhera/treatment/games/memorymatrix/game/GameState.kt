package com.example.memorymatrix.game

data class GameState(
    val currentRound: Int = 1,
    val highlightedCells: Set<Pair<Int, Int>> = emptySet(),
    val distractorCells: Set<Pair<Int, Int>> = emptySet(),
    val userSelectedCells: Set<Pair<Int, Int>> = emptySet(),
    val wrongSelections: Set<Pair<Int, Int>> = emptySet(),
    val isDisplayingPattern: Boolean = true,
    val isReadyStage: Boolean = true,
    val isRoundActive: Boolean = false,
    val roundAccuracies: List<Float> = emptyList(),
    val reactionTimes: List<Long> = emptyList()
)