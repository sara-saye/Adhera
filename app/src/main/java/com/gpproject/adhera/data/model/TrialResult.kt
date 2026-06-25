package com.gpproject.adhera.data.model

data class TrialResult(
    val trialNumber: Int,
    val load: Int,
    val correctAnswer: Boolean,
    val userAnswer: Boolean?,
    val isCorrect: Boolean?,
    val reactionTime: Long?
)