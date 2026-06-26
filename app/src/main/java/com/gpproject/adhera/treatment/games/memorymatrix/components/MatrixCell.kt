package com.example.memorymatrix.ui.components

import android.graphics.BlurMaskFilter
import android.graphics.Paint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.example.memorymatrix.ui.theme.*
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MatrixCell(
    isHighlighted: Boolean,
    isDistractor: Boolean,
    isSelected: Boolean,
    isWrong: Boolean,
    isDisplayingPattern: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val targetColor = when {
        isDisplayingPattern && isHighlighted -> NeonBlue
        isSelected -> NeonGreen
        isWrong -> NeonRed
        else -> CellBase
    }

    val color by animateColorAsState(targetValue = targetColor, animationSpec = tween(300))

    val glowIntensity by animateFloatAsState(
        targetValue = if (targetColor != CellBase || (isDisplayingPattern && isDistractor)) {
            20f
        } else 0f,
        animationSpec = tween(300)
    )

    Box(
        modifier = modifier
            .padding(5.dp)
            .aspectRatio(1f)
            .drawBehind {
                if (glowIntensity > 0f) {
                    val glowColor = if (isDisplayingPattern && isDistractor) NeonOrange else color
                    val paint = Paint().apply {
                        isAntiAlias = true
                        this.color = glowColor.toArgb()
                        maskFilter = BlurMaskFilter(glowIntensity, BlurMaskFilter.Blur.OUTER)
                    }
                    drawContext.canvas.nativeCanvas.drawRoundRect(
                        0f, 0f, size.width, size.height,
                        20f, 20f, paint
                    )
                }

                if (isDisplayingPattern && isDistractor) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val radius = size.width * 0.3f
                    val path = Path().apply {
                        for (i in 0 until 6) {
                            val angle = Math.toRadians((i * 60).toDouble())
                            val x = center.x + radius * cos(angle).toFloat()
                            val y = center.y + radius * sin(angle).toFloat()
                            if (i == 0) moveTo(x, y) else lineTo(x, y)
                        }
                        close()
                    }
                    drawPath(
                        path = path,
                        color = NeonOrange,
                        style = Stroke(width = 3.dp.toPx())
                    )
                }
            }
            .clip(RoundedCornerShape(8.dp))
            .background(if (targetColor == CellBase) CellBase.copy(alpha = 0.85f) else color.copy(alpha = 0.85f))
            .background(
                if (targetColor != CellBase && !(isDisplayingPattern && isDistractor)) {
                    color.copy(alpha = 0.2f)
                } else Color.Transparent
            )
            .clickable { onClick() }
    )
}