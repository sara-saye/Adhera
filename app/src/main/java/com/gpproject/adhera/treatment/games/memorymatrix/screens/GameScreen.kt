package com.example.memorymatrix.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gpproject.adhera.R
import com.example.memorymatrix.ui.components.MatrixGrid
import com.example.memorymatrix.ui.components.PerformanceBar
import com.example.memorymatrix.ui.theme.DeepSpace
import com.example.memorymatrix.ui.theme.NeonBlue
import com.example.memorymatrix.ui.theme.NeonGreen
import com.example.memorymatrix.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    onLevelFinished: (Float) -> Unit
) {
    val gameState by viewModel.gameState.collectAsState()
    val difficultyState by viewModel.difficultyState.collectAsState()


    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.matrix),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            DeepSpace.copy(alpha = 0.85f),
                            DeepSpace.copy(alpha = 0.95f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(top = 24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "LEVEL ${difficultyState.level}",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "ROUND ${gameState.currentRound}/${difficultyState.totalRoundsInLevel}",
                        color = Color(0xFF8D99AE),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))

                val completedRounds = gameState.currentRound - 1
                val progress = completedRounds.toFloat() / difficultyState.totalRoundsInLevel

                PerformanceBar(progress = progress)
            }

            Text(
                text = if (gameState.isDisplayingPattern) "WATCH & MEMORIZE THE PATTERN" else "REPLICATE THE PATTERN",
                color = if (gameState.isDisplayingPattern) NeonBlue else NeonGreen,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            MatrixGrid(
                gridSize = difficultyState.gridSize,
                highlightedCells = gameState.highlightedCells,
                distractorCells = gameState.distractorCells,
                userSelectedCells = gameState.userSelectedCells,
                wrongSelections = gameState.wrongSelections,
                isDisplayingPattern = gameState.isDisplayingPattern,
                onCellClick = { r, c ->
                    viewModel.onCellClick(r, c, onLevelFinished)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
