package com.almari.feature.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.LockOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.almari.core.designsystem.LocalAlmariColors
import com.almari.core.designsystem.LocalAlmariTypography
import com.almari.shared.feature.home.WardrobeItem
import com.almari.shared.feature.home.WardrobeRail
import com.almari.shared.feature.home.WardrobeSlot

@Composable
internal fun WardrobeRailCard(
    rail: WardrobeRail,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onToggleLock: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalAlmariColors.current
    val tint by animateColorAsState(targetValue = railTint(rail.slot), label = "rail tint")

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(tint, RoundedCornerShape(18.dp)),
    ) {
        Text(
            text = rail.slot.label,
            color = colors.studioInk,
            style = LocalAlmariTypography.current.label,
            modifier = Modifier.padding(start = 12.dp, top = 9.dp),
        )

        IconButton(
            onClick = onToggleLock,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(5.dp)
                .size(34.dp)
                .background(Color.White.copy(alpha = 0.88f), CircleShape)
                .semantics { contentDescription = "${if (rail.isLocked) "Unlock" else "Lock"} ${rail.slot.label.lowercase()}" },
        ) {
            Icon(
                imageVector = if (rail.isLocked) Icons.Rounded.Lock else Icons.Rounded.LockOpen,
                contentDescription = null,
                tint = colors.studioInk,
                modifier = Modifier.size(17.dp),
            )
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 22.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(onClick = onPrevious, enabled = !rail.isLocked, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Rounded.ChevronLeft, "Previous ${rail.slot.label.lowercase()}", tint = colors.studioInk)
            }
            RailGarment(item = rail.previousItem, alpha = 0.38f, modifier = Modifier.weight(0.7f))
            RailGarment(item = rail.selectedItem, alpha = 1f, modifier = Modifier.weight(1.25f))
            RailGarment(item = rail.nextItem, alpha = 0.38f, modifier = Modifier.weight(0.7f))
            IconButton(onClick = onNext, enabled = !rail.isLocked, modifier = Modifier.size(36.dp)) {
                Icon(Icons.Rounded.ChevronRight, "Next ${rail.slot.label.lowercase()}", tint = colors.studioInk)
            }
        }
    }
}

@Composable
private fun RailGarment(item: WardrobeItem, alpha: Float, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxHeight().alpha(alpha), contentAlignment = Alignment.Center) {
        StudioGarmentVisual(item = item, modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun railTint(slot: WardrobeSlot): Color {
    val colors = LocalAlmariColors.current
    return when (slot) {
        WardrobeSlot.Head -> colors.softLilac
        WardrobeSlot.Top -> colors.softPink
        WardrobeSlot.Layer -> colors.mintTint
        WardrobeSlot.Bottom -> colors.skyTint
        WardrobeSlot.Shoes -> colors.butterTint
    }
}
