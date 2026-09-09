package com.almari.feature.home.components

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
import com.almari.core.designsystem.LocalAlmariColors
import com.almari.core.designsystem.LocalAlmariSizes
import com.almari.core.designsystem.LocalAlmariSpacing
import com.almari.core.designsystem.LocalAlmariTypography
import com.almari.shared.feature.home.WardrobeItem
import com.almari.shared.feature.home.WardrobeRail

@Composable
internal fun WardrobeRailCard(
    rail: WardrobeRail,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onToggleLock: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalAlmariColors.current
    val background by animateColorAsState(
        targetValue = if (rail.isLocked) colors.cobaltSoft else colors.white,
    )
    val border by animateColorAsState(
        targetValue = if (rail.isLocked) colors.cobalt.copy(alpha = 0.35f) else colors.border,
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(LocalAlmariSizes.current.railHeight)
            .shadow(2.dp, RoundedCornerShape(LocalAlmariSizes.current.cardRadius))
            .background(background, RoundedCornerShape(LocalAlmariSizes.current.cardRadius))
            .border(1.dp, border, RoundedCornerShape(LocalAlmariSizes.current.cardRadius)),
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = LocalAlmariSpacing.current.md, top = LocalAlmariSpacing.current.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = rail.slot.label,
                color = if (rail.isLocked) colors.cobalt else colors.ink,
                style = LocalAlmariTypography.current.label,
            )
            Spacer(Modifier.width(LocalAlmariSpacing.current.xs))
            Text(
                text = if (rail.isLocked) "LOCKED" else "${rail.selectedIndex + 1}/${rail.items.size}",
                color = if (rail.isLocked) colors.cobalt else colors.muted,
                style = LocalAlmariTypography.current.caption,
                modifier = Modifier
                    .background(
                        if (rail.isLocked) colors.cobalt.copy(alpha = 0.1f) else colors.cloud,
                        CircleShape,
                    )
                    .padding(horizontal = LocalAlmariSpacing.current.xs, vertical = LocalAlmariSpacing.current.xxs),
            )
        }

        IconButton(
            onClick = onToggleLock,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = LocalAlmariSpacing.current.xxs, end = LocalAlmariSpacing.current.xxs)
                .semantics { contentDescription = "${if (rail.isLocked) "Unlock" else "Lock"} ${rail.slot.label}" },
        ) {
            Icon(
                imageVector = if (rail.isLocked) Icons.Rounded.Lock else Icons.Rounded.LockOpen,
                contentDescription = null,
                tint = if (rail.isLocked) colors.white else colors.muted,
                modifier = Modifier
                    .size(LocalAlmariSizes.current.iconSmall)
                    .background(if (rail.isLocked) colors.cobalt else colors.cloud, CircleShape)
                    .padding(2.dp),
            )
        }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = LocalAlmariSpacing.current.lg),
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
                    top = if (showName) LocalAlmariSpacing.current.sm else LocalAlmariSpacing.current.md,
                    bottom = if (showName) LocalAlmariSpacing.current.md else LocalAlmariSpacing.current.xs,
                )
                .fillMaxSize()
                .alpha(alpha),
        )
        if (showName) {
            Text(
                text = item.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = LocalAlmariColors.current.ink,
                style = LocalAlmariTypography.current.caption,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = LocalAlmariSpacing.current.xxs),
            )
        }
    }
}
