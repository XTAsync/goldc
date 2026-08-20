package com.example.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.TradingGreen
import com.example.ui.theme.TradingRed

@Composable
fun SparklineChart(
    points: List<Float>,
    isPositive: Boolean,
    modifier: Modifier = Modifier,
    useBlueForPositive: Boolean = true,
    showGradientFill: Boolean = true,
    strokeWidth: Dp = 1.75.dp
) {
    val lineColor = when {
        !isPositive -> TradingRed
        useBlueForPositive -> ElectricBlue
        else -> TradingGreen
    }

    val gradientTopColor = lineColor.copy(alpha = 0.28f)
    val gradientBottomColor = lineColor.copy(alpha = 0.0f)

    Canvas(modifier = modifier) {
        if (points.size < 2) return@Canvas

        val width = size.width
        val height = size.height
        val minVal = points.minOrNull() ?: 0f
        val maxVal = points.maxOrNull() ?: 1f
        val range = (maxVal - minVal).coerceAtLeast(0.0001f)

        val paddingY = height * 0.1f
        val usableHeight = height - (paddingY * 2)

        val coords = points.mapIndexed { index, value ->
            val x = (index.toFloat() / (points.size - 1)) * width
            val normalizedY = 1f - ((value - minVal) / range)
            val y = paddingY + (normalizedY * usableHeight)
            Offset(x, y)
        }

        // Build smooth bezier curve path
        val strokePath = Path().apply {
            moveTo(coords.first().x, coords.first().y)
            for (i in 0 until coords.size - 1) {
                val current = coords[i]
                val next = coords[i + 1]
                val controlX1 = current.x + (next.x - current.x) / 2f
                val controlY1 = current.y
                val controlX2 = current.x + (next.x - current.x) / 2f
                val controlY2 = next.y
                cubicTo(controlX1, controlY1, controlX2, controlY2, next.x, next.y)
            }
        }

        // Draw gradient area underneath
        if (showGradientFill) {
            val fillPath = Path().apply {
                addPath(strokePath)
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(gradientTopColor, gradientBottomColor),
                    startY = 0f,
                    endY = height
                )
            )
        }

        // Draw the sparkline stroke
        drawPath(
            path = strokePath,
            color = lineColor,
            style = Stroke(
                width = strokeWidth.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Draw end glowing point
        coords.lastOrNull()?.let { lastPoint ->
            drawCircle(
                color = lineColor,
                radius = strokeWidth.toPx() * 1.3f,
                center = lastPoint
            )
        }
    }
}
