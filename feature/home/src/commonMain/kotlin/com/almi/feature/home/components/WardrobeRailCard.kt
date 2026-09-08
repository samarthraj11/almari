package com.almi.feature.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.almi.core.designsystem.LocalAlmiColors
import com.almi.core.designsystem.LocalAlmiSizes
import com.almi.core.designsystem.LocalAlmiSpacing
import com.almi.core.designsystem.LocalAlmiTypography
import com.almi.shared.feature.home.WardrobeItem
import com.almi.shared.feature.home.WardrobeRail

@Composable
internal fun WardrobeRailCard(
    rail: WardrobeRail,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onToggleLock: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalAlmiColors.current
    val background by animateColorAsState(
        targetValue = if (rail.isLocked) colors.cobaltSoft else colors.white,
    )
    val border by animateColorAsState(
        targetValue = if (rail.isLocked) colors.cobalt.copy(alpha = 0.35f) else colors.border,
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(LocalAlmiSizes.current.railHeight)
            .shadow(2.dp, RoundedCornerShape(LocalAlmiSizes.current.cardRadius))
            .background(background, RoundedCornerShape(LocalAlmiSizes.current.cardRadius))
            .border(1.dp, border, RoundedCornerShape(LocalAlmiSizes.current.cardRadius)),
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = LocalAlmiSpacing.current.md, top = LocalAlmiSpacing.current.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = rail.slot.label,
                color = if (rail.isLocked) colors.cobalt else colors.ink,
                style = LocalAlmiTypography.current.label,
            )
            Spacer(Modifier.width(LocalAlmiSpacing.current.xs))
            Text(
                text = if (rail.isLocked) "LOCKED" else "${rail.selectedIndex + 1}/${rail.items.size}",
                color = if (rail.isLocked) colors.cobalt else colors.muted,
                style = LocalAlmiTypography.current.caption,
                modifier = Modifier
                    .background(
                        if (rail.isLocked) colors.cobalt.copy(alpha = 0.1f) else colors.cloud,
                        CircleShape,
                    )
                    .padding(horizontal = LocalAlmiSpacing.current.xs, vertical = LocalAlmiSpacing.current.xxs),
            )
        }

        IconButton(
            onClick = onToggleLock,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = LocalAlmiSpacing.current.xxs, end = LocalAlmiSpacing.current.xxs)
                .semantics { contentDescription = "${if (rail.isLocked) "Unlock" else "Lock"} ${rail.slot.label}" },
        ) {
            Icon(
                imageVector = if (rail.isLocked) Icons.Rounded.Lock else Icons.Rounded.LockOpen,
                contentDescription = null,
                tint = if (rail.isLocked) colors.white else colors.muted,
                modifier = Modifier
                    .size(LocalAlmiSizes.current.iconSmall)
                    .background(if (rail.isLocked) colors.cobalt else colors.cloud, CircleShape)
                    .padding(2.dp),
            )
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = LocalAlmiSpacing.current.lg),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onPrevious, enabled = !rail.isLocked) {
                Icon(
                    Icons.Rounded.ChevronLeft,
                    contentDescription = "Previous ${rail.slot.label.lowercase()} item",
                    tint = if (rail.isLocked) colors.muted.copy(alpha = 0.35f) else colors.muted,
                )
            }
            GarmentPreview(item = rail.previousItem, alpha = 0.28f, modifier = Modifier.weight(0.75f))
            GarmentPreview(item = rail.selectedItem, alpha = 1f, modifier = Modifier.weight(1.15f), showName = true)
            GarmentPreview(item = rail.nextItem, alpha = 0.28f, modifier = Modifier.weight(0.75f))
            IconButton(onClick = onNext, enabled = !rail.isLocked) {
                Icon(
                    Icons.Rounded.ChevronRight,
                    contentDescription = "Next ${rail.slot.label.lowercase()} item",
                    tint = if (rail.isLocked) colors.muted.copy(alpha = 0.35f) else colors.muted,
                )
            }
        }
    }
}

@Composable
private fun GarmentPreview(
    item: WardrobeItem,
    alpha: Float,
    modifier: Modifier = Modifier,
    showName: Boolean = false,
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        GarmentIllustration(
            shape = item.shape,
            color = Color(item.colorValue),
            modifier = Modifier
                .padding(
                    top = if (showName) LocalAlmiSpacing.current.sm else LocalAlmiSpacing.current.md,
                    bottom = if (showName) LocalAlmiSpacing.current.md else LocalAlmiSpacing.current.xs,
                )
                .fillMaxSize()
                .alpha(alpha),
        )
        if (showName) {
            Text(
                text = item.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = LocalAlmiColors.current.ink,
                style = LocalAlmiTypography.current.caption,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = LocalAlmiSpacing.current.xxs),
            )
        }
    }
}
