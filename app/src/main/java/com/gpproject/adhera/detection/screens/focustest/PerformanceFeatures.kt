package com.gpproject.adhera.detection.screens.focustest

data class PerformanceFeatures(
    val maxPupil: Double = 0.0,
    val medianPupil: Double = 0.0,

    val maxContraction: Double = 0.0,
    val meanContraction: Double = 0.0,

    val maxDilation: Double = 0.0,
    val meanDilation: Double = 0.0,

    val lowLoadCorrect: Int,
    val lowLoadIncorrect: Int,
    val highLoadCorrect: Int,
    val highLoadIncorrect: Int,
    val lowLoadAccuracy: Float,
    val highLoadAccuracy: Float,
    val meanReactionTime: Double,
    val lowLoadMeanRT: Double,
    val highLoadMeanRT: Double
)