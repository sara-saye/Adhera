package com.example.memorymatrix.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.memorymatrix.ui.theme.CellBase
import com.example.memorymatrix.ui.theme.NeonBlue

@Composable
fun MatrixGrid(
    gridSize: Int,
    highlightedCells: Set<Pair<Int, Int>>,
    distractorCells: Set<Pair<Int, Int>>,
    userSelectedCells: Set<Pair<Int, Int>>,
    wrongSelections: Set<Pair<Int, Int>>,
    isDisplayingPattern: Boolean,
    onCellClick: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .drawBehind {
                val strokeWidth = 2.dp.toPx()
                val cornerLength = 24.dp.toPx()
                val padding = 4.dp.toPx()

                drawRoundRect(
                    color = NeonBlue.copy(alpha = 0.15f),
                    size = size,
                    style = Stroke(width = strokeWidth / 2)
                )

                // زوايا نيون مزخرفة للـ Grid (Sci-Fi Border Corners)
                drawLine(NeonBlue, Offset(-padding, -padding), Offset(cornerLength, -padding), strokeWidth)
                drawLine(NeonBlue, Offset(-padding, -padding), Offset(-padding, cornerLength), strokeWidth)

                drawLine(NeonBlue, Offset(size.width + padding, -padding), Offset(size.width - cornerLength, -padding), strokeWidth)
                drawLine(NeonBlue, Offset(size.width + padding, -padding), Offset(size.width + padding, cornerLength), strokeWidth)

                drawLine(NeonBlue, Offset(-padding, size.height + padding), Offset(cornerLength, size.height + padding), strokeWidth)
                drawLine(NeonBlue, Offset(-padding, size.height + padding), Offset(-padding, size.height - cornerLength), strokeWidth)

                drawLine(NeonBlue, Offset(size.width + padding, size.height + padding), Offset(size.width - cornerLength, size.height + padding), strokeWidth)
                drawLine(NeonBlue, Offset(size.width + padding, size.height + padding), Offset(size.width + padding, size.height - cornerLength), strokeWidth)
            }
            .clip(RoundedCornerShape(12.dp))
            .background(CellBase.copy(alpha = 0.4f))
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            for (row in 0 until gridSize) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    for (col in 0 until gridSize) {
                        val cell = Pair(row, col)
                        MatrixCell(
                            isHighlighted = highlightedCells.contains(cell),
                            isDistractor = distractorCells.contains(cell),
                            isSelected = userSelectedCells.contains(cell),
                            isWrong = wrongSelections.contains(cell),
                            isDisplayingPattern = isDisplayingPattern,
                            onClick = { onCellClick(row, col) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}