package com.almari.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.almari.core.designsystem.LocalAlmariColors

@Composable
internal fun StudioChip(
    label: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val colors = LocalAlmariColors.current
    Box(
        modifier = modifier
            .background(if (selected) colors.acidLime else Color(0xFFF5F3F0), RoundedCornerShape(18.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 13.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = colors.studioInk,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Black else FontWeight.Medium,
        )
    }
}

@Composable
internal fun NeonAction(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalAlmariColors.current
    Box(
        modifier = modifier
            .height(54.dp)
            .background(
                Brush.horizontalGradient(listOf(colors.hotPink, colors.electricBlue, colors.acidLime)),
                RoundedCornerShape(28.dp),
            )
            .padding(2.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.studioInk, RoundedCornerShape(26.dp))
                .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(26.dp))
                .clickable(onClick = onClick),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center,
        ) {
            Icon(Icons.Rounded.Shuffle, contentDescription = null, tint = Color.White)
            Spacer(Modifier.width(9.dp))
            Text(label, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black)
        }
    }
}
