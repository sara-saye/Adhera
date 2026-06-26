package com.example.memorymatrix.game

class DifficultyEngine {

    fun calculateNextDifficulty(currentState: DifficultyState, averageAccuracy: Float): DifficultyState {
        val (upgradeThreshold, downgradeThreshold) = when {
            currentState.level <= 2 -> Pair(0.50f, 0.30f)
            currentState.level <= 5 -> Pair(0.65f, 0.50f)
            currentState.level <= 10 -> Pair(0.75f, 0.55f)
            else -> Pair(0.80f, 0.65f)
        }

        val nextLevel = when {
            averageAccuracy >= upgradeThreshold -> {
                currentState.level + 1
            }
            averageAccuracy < downgradeThreshold -> {
                if (currentState.level > 1) currentState.level - 1 else 1
            }
            else -> {
                currentState.level
            }
        }

        val nextGridSize = when {
            nextLevel <= 2 -> 4
            nextLevel <= 5 -> 5
            else -> 6
        }

        val nextCorrectCount = (3 + ((nextLevel - 1) / 3)).coerceAtMost(7)

        val nextDistractorsCount = when {
            nextLevel <= 2 -> 0
            nextLevel <= 4 -> 2
            else -> 4
        }

        val baseTime = 1500L
        val timeReduction = (nextLevel - 1) * 150L
        val nextDisplayTime = (baseTime - timeReduction).coerceAtLeast(700L)

        val nextTotalRounds = 3 + ((nextLevel - 1) / 2)

        return DifficultyState(
            level = nextLevel,
            gridSize = nextGridSize,
            correctCellsCount = nextCorrectCount,
            displayTimeMs = nextDisplayTime,
            totalRoundsInLevel = nextTotalRounds,
            distractorsCount = nextDistractorsCount
        )
    }
}