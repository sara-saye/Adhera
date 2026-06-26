package com.gpproject.adhera.detection.screens.focustest

class CalculatePerformanceFeaturesUseCase {

    operator fun invoke(
        results: List<TrialResult>
    ): PerformanceFeatures {

        val lowLoadCorrect =
            results.count {
                it.load == 1 && it.isCorrect == true
            }

        val lowLoadIncorrect =
            results.count {
                it.load == 1 && it.isCorrect == false
            }

        val highLoadCorrect =
            results.count {
                it.load == 2 && it.isCorrect == true
            }

        val highLoadIncorrect =
            results.count {
                it.load == 2 && it.isCorrect == false
            }

        val lowLoadTotal =
            lowLoadCorrect + lowLoadIncorrect

        val highLoadTotal =
            highLoadCorrect + highLoadIncorrect

        val lowLoadAccuracy =
            if (lowLoadTotal > 0)
                lowLoadCorrect.toFloat() / lowLoadTotal
            else 0f

        val highLoadAccuracy =
            if (highLoadTotal > 0)
                highLoadCorrect.toFloat() / highLoadTotal
            else 0f

        val meanReactionTime =
            results
                .mapNotNull { it.reactionTime }
                .average()

        val lowLoadMeanRT =
            results
                .filter { it.load == 1 }
                .mapNotNull { it.reactionTime }
                .average()

        val highLoadMeanRT =
            results
                .filter { it.load == 2 }
                .mapNotNull { it.reactionTime }
                .average()

        return PerformanceFeatures(
            maxPupil = 0.0,
            medianPupil = 0.0,

            maxContraction = 0.0,
            meanContraction = 0.0,

            maxDilation = 0.0,
            meanDilation = 0.0,
            lowLoadCorrect,
            lowLoadIncorrect,
            highLoadCorrect,
            highLoadIncorrect,
            lowLoadAccuracy,
            highLoadAccuracy,
            meanReactionTime,
            lowLoadMeanRT,
            highLoadMeanRT
        )
    }
}