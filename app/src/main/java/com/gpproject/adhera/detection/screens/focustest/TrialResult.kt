package com.gpproject.adhera.detection.screens.focustest

data class TrialResult(
    val trialNumber: Int,
    val load: Int,
    val correctAnswer: Boolean,
    val userAnswer: Boolean?,
    val isCorrect: Boolean?,
    val reactionTime: Long?
)