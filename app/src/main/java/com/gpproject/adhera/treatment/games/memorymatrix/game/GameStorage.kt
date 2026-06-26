package com.example.memorymatrix.game

import android.content.Context
import android.content.SharedPreferences

class GameStorage(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("MemoryMatrixPrefs", Context.MODE_PRIVATE)

    fun saveGame(state: DifficultyState) {
        prefs.edit().apply {
            putInt("saved_level", state.level)
            putInt("saved_grid_size", state.gridSize)
            putInt("saved_correct_count", state.correctCellsCount)
            putLong("saved_display_time", state.displayTimeMs)
            putInt("saved_distractors_count", state.distractorsCount)
            putBoolean("has_saved_game", true)
            apply()
        }
    }

    fun loadGame(): DifficultyState? {
        if (!hasSavedGame()) return null
        return DifficultyState(
            level = prefs.getInt("saved_level", 1),
            gridSize = prefs.getInt("saved_grid_size", 4),
            correctCellsCount = prefs.getInt("saved_correct_count", 3),
            displayTimeMs = prefs.getLong("saved_display_time", 1500L),
            totalRoundsInLevel = 3,
            distractorsCount = prefs.getInt("saved_distractors_count", 0)
        )
    }

    fun hasSavedGame(): Boolean {
        return prefs.getBoolean("has_saved_game", false)
    }

    fun clearSavedGame() {
        prefs.edit().remove("has_saved_game").apply()
    }
}