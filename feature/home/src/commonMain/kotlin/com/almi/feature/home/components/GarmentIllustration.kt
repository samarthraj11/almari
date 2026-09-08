package com.almi.feature.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.luminance
import com.almi.shared.feature.home.GarmentShape

@Composable
internal fun GarmentIllustration(
    shape: GarmentShape,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val outline = if (color.luminance() > 0.72f) Color(0xFFB9B1A4) else color.copy(alpha = 0.82f)
        when (shape) {
            GarmentShape.Cap -> {
                drawArc(
                    color = color,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = true,
                    topLeft = Offset(size.width * 0.2f, size.height * 0.2f),
                    size = Size(size.width * 0.58f, size.height * 0.62f),
                )
                drawOval(
                    color = outline,
                    topLeft = Offset(size.width * 0.5f, size.height * 0.58f),
                    size = Size(size.width * 0.36f, size.height * 0.12f),
                )
            }

            GarmentShape.Kurta -> drawGarmentBody(
                color = color,
                outline = outline,
                longBody = true,
                hasOpening = true,
            )

            GarmentShape.Shirt -> drawGarmentBody(
                color = color,
                outline = outline,
                longBody = false,
                hasOpening = true,
            )

            GarmentShape.Jacket -> drawGarmentBody(
                color = color,
                outline = outline,
                longBody = false,
                hasOpening = true,
            )

            GarmentShape.Trousers -> {
                val trousers = Path().apply {
                    moveTo(size.width * 0.28f, size.height * 0.12f)
                    lineTo(size.width * 0.72f, size.height * 0.12f)
                    lineTo(size.width * 0.84f, size.height * 0.9f)
                    lineTo(size.width * 0.55f, size.height * 0.9f)
                    lineTo(size.width * 0.5f, size.height * 0.45f)
                    lineTo(size.width * 0.45f, size.height * 0.9f)
                    lineTo(size.width * 0.16f, size.height * 0.9f)
                    close()
                }
                drawPath(trousers, color)
                drawLine(
                    outline,
                    Offset(size.width * 0.28f, size.height * 0.2f),
                    Offset(size.width * 0.72f, size.height * 0.2f),
                    strokeWidth = size.minDimension * 0.04f,
                )
            }

            GarmentShape.Skirt -> {
                val skirt = Path().apply {
                    moveTo(size.width * 0.35f, size.height * 0.15f)
                    lineTo(size.width * 0.65f, size.height * 0.15f)
                    lineTo(size.width * 0.82f, size.height * 0.88f)
                    lineTo(size.width * 0.18f, size.height * 0.88f)
                    close()
                }
                drawPath(skirt, color)
            }

            GarmentShape.Sneakers -> {
                val shoe = Path().apply {
                    moveTo(size.width * 0.13f, size.height * 0.43f)
                    lineTo(size.width * 0.42f, size.height * 0.25f)
                    lineTo(size.width * 0.6f, size.height * 0.53f)
                    lineTo(size.width * 0.87f, size.height * 0.62f)
                    quadraticTo(
                        size.width * 0.94f,
                        size.height * 0.67f,
                        size.width * 0.86f,
                        size.height * 0.78f,
                    )
                    lineTo(size.width * 0.22f, size.height * 0.78f)
                    quadraticTo(
                        size.width * 0.1f,
                        size.height * 0.7f,
                        size.width * 0.13f,
                        size.height * 0.43f,
                    )
                    close()
                }
                drawPath(shoe, color)
                drawLine(
                    outline,
                    Offset(size.width * 0.2f, size.height * 0.69f),
                    Offset(size.width * 0.86f, size.height * 0.69f),
                    strokeWidth = size.minDimension * 0.045f,
                )
            }

            GarmentShape.Sandals -> {
                drawRoundRect(
                    color = color,
                    topLeft = Offset(size.width * 0.16f, size.height * 0.48f),
                    size = Size(size.width * 0.68f, size.height * 0.22f),
                    cornerRadius = CornerRadius(size.height * 0.11f),
                )
                drawArc(
                    color = outline,
                    startAngle = 190f,
                    sweepAngle = 160f,
                    useCenter = false,
                    topLeft = Offset(size.width * 0.28f, size.height * 0.25f),
                    size = Size(size.width * 0.44f, size.height * 0.42f),
                    style = Stroke(width = size.minDimension * 0.07f),
                )
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawGarmentBody(
    color: Color,
    outline: Color,
    longBody: Boolean,
    hasOpening: Boolean,
) {
    val hem = if (longBody) size.height * 0.92f else size.height * 0.8f
    val body = Path().apply {
        moveTo(size.width * 0.35f, size.height * 0.16f)
        lineTo(size.width * 0.18f, size.height * 0.27f)
        lineTo(size.width * 0.07f, size.height * 0.55f)
        lineTo(size.width * 0.24f, size.height * 0.62f)
        lineTo(size.width * 0.31f, size.height * 0.43f)
        lineTo(size.width * 0.28f, hem)
        lineTo(size.width * 0.72f, hem)
        lineTo(size.width * 0.69f, size.height * 0.43f)
        lineTo(size.width * 0.76f, size.height * 0.62f)
        lineTo(size.width * 0.93f, size.height * 0.55f)
        lineTo(size.width * 0.82f, size.height * 0.27f)
        lineTo(size.width * 0.65f, size.height * 0.16f)
        quadraticTo(size.width * 0.5f, size.height * 0.3f, size.width * 0.35f, size.height * 0.16f)
        close()
    }
    drawPath(body, color)
    if (hasOpening) {
        drawLine(
            color = outline,
            start = Offset(size.width * 0.5f, size.height * 0.27f),
            end = Offset(size.width * 0.5f, hem * 0.92f),
            strokeWidth = size.minDimension * 0.025f,
        )
    }
}
