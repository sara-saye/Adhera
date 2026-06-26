package com.example.memorymatrix.game

data class DifficultyState(
    val level: Int,
    val gridSize: Int,
    val correctCellsCount: Int,
    val displayTimeMs: Long,
    val totalRoundsInLevel: Int = 3,
    val distractorsCount: Int = 0
)