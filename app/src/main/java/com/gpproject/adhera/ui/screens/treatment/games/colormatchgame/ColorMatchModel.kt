package com.gpproject.adhera.ui.screens.treatment.games.colormatchgame

import androidx.compose.ui.graphics.Color

data class ColorWord(
    val word: String,
    val textColor: Color,
    val colorName: String
)

data class RoundData(
    val left: ColorWord,
    val right: ColorWord,
    val answer: Boolean
)

sealed class ColorMatchScreenState {
    object Home : ColorMatchScreenState()
    object HowToPlay : ColorMatchScreenState()
    object Game : ColorMatchScreenState()
    data class GameOver(val score: Int, val best: Int) : ColorMatchScreenState()
}

object ColorMatchConstants {
    val COLORS = listOf(
        Triple("red",    Color(0xFFE53935), "red"),
        Triple("blue",   Color(0xFF1E88E5), "blue"),
        Triple("green",  Color(0xFF43A047), "green"),
        Triple("yellow", Color(0xFFFDD835), "yellow"),
        Triple("orange", Color(0xFFFB8C00), "orange"),
        Triple("purple", Color(0xFF8E24AA), "purple"),
        Triple("black",  Color(0xFF212121), "black"),
        Triple("pink",   Color(0xFFE91E63), "pink"),
    )

    // الألوان الثابتة الخاصة بالـ Theme الخشبي للعبة
    val WoodBrown    = Color(0xFF8B6343)
    val WoodDark     = Color(0xFF6D4C2F)
    val WoodLight    = Color(0xFFA07850)
    val CardBg       = Color(0xFFFFF8F0)
    val AccentOrange = Color(0xFFE64A19)
    val TealBtn      = Color(0xFF1B3A5C)
    val TextLight    = Color(0xFFEEE0CC)
    val TextMuted    = Color(0xFFD7C4A8)
}

fun randomColorWord(): ColorWord {
    val word = ColorMatchConstants.COLORS.random()
    val color = ColorMatchConstants.COLORS.random()
    return ColorWord(word = word.first, textColor = color.second, colorName = color.third)
}

fun generateRound(): RoundData {
    val isMatch = (0..1).random() == 1
    val left = randomColorWord()
    val right: ColorWord
    if (isMatch) {
        val matchColor = ColorMatchConstants.COLORS.first { it.first == left.word }
        right = ColorWord(word = ColorMatchConstants.COLORS.random().first, textColor = matchColor.second, colorName = matchColor.third)
    } else {
        var r: ColorWord
        do { r = randomColorWord() } while (r.colorName == left.word)
        right = r
    }
    return RoundData(left = left, right = right, answer = isMatch)
}