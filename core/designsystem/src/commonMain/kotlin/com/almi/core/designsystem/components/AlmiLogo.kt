package com.almi.core.designsystem.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.almi.core.designsystem.LocalAlmiColors

@Composable
fun AlmiLogo(
    modifier: Modifier = Modifier,
    size: Dp = 38.dp,
    backgroundColor: Color = LocalAlmiColors.current.cobalt,
    markColor: Color = LocalAlmiColors.current.white,
    accentColor: Color = LocalAlmiColors.current.lime,
) {
    Box(
        modifier = modifier
            .size(size)
            .background(backgroundColor, RoundedCornerShape(size * 0.34f)),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(size * 0.66f)) {
            val stroke = this.size.minDimension * 0.105f
            val left = this.size.width * 0.17f
            val right = this.size.width * 0.83f
            val top = this.size.height * 0.16f
            val bottom = this.size.height * 0.84f
            drawRoundRect(
                color = markColor,
                topLeft = Offset(left, top),
                size = Size(right - left, bottom - top),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(stroke),
                style = Stroke(width = stroke),
            )
            drawLine(
                color = markColor,
                start = Offset(this.size.width * 0.5f, top),
                end = Offset(this.size.width * 0.5f, bottom),
                strokeWidth = stroke * 0.72f,
            )
            val hanger = Path().apply {
                moveTo(this@Canvas.size.width * 0.32f, this@Canvas.size.height * 0.62f)
                lineTo(this@Canvas.size.width * 0.5f, this@Canvas.size.height * 0.48f)
                lineTo(this@Canvas.size.width * 0.68f, this@Canvas.size.height * 0.62f)
            }
            drawPath(hanger, markColor, style = Stroke(width = stroke * 0.72f))
            drawCircle(
                color = accentColor,
                radius = stroke * 0.34f,
                center = Offset(this.size.width * 0.5f, this.size.height * 0.49f),
            )
        }
    }
}
