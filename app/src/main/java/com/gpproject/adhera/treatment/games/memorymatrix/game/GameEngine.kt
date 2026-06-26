package com.example.memorymatrix.game

class GameEngine {

    fun generateRoundPattern(
        gridSize: Int,
        correctCount: Int,
        distractorsCount: Int
    ): Pair<Set<Pair<Int, Int>>, Set<Pair<Int, Int>>> {

        val allCells = mutableListOf<Pair<Int, Int>>()
        for (r in 0 until gridSize) {
            for (c in 0 until gridSize) {
                allCells.add(Pair(r, c))
            }
        }

        allCells.shuffle()
        val correctCells = allCells.take(correctCount).toSet()

        val remainingCells = allCells.drop(correctCount)
        val distractorCells = remainingCells.take(distractorsCount).toSet()

        return Pair(correctCells, distractorCells)
    }
}