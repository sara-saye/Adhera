package com.example.memorymatrix.data

data class SessionResult(
    val level: Int,
    val averageAccuracy: Float,
    val averageReactionTimeMs: Long,
    val totalRoundsPlayed: Int,
    val timestamp: Long = System.currentTimeMillis()
)