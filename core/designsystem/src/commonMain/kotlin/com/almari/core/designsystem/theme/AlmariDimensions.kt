package com.almari.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class AlmariSpacing(
    val xxs: Dp = 4.dp,
    val xs: Dp = 8.dp,
    val sm: Dp = 12.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 20.dp,
    val xl: Dp = 24.dp,
)

@Immutable
data class AlmariSizes(
    val iconSmall: Dp = 18.dp,
    val iconMedium: Dp = 22.dp,
    val iconLarge: Dp = 26.dp,
    val tapTarget: Dp = 44.dp,
    val logo: Dp = 38.dp,
    val railHeight: Dp = 104.dp,
    val cardRadius: Dp = 22.dp,
    val buttonRadius: Dp = 18.dp,
)
