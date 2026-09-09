package com.almi.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class AlmiColors(
    val ink: Color = Color(0xFF11131A),
    val cobalt: Color = Color(0xFF3157F6),
    val cobaltPressed: Color = Color(0xFF2445D8),
    val cobaltSoft: Color = Color(0xFFEEF3FF),
    val cloud: Color = Color(0xFFF5F6FA),
    val white: Color = Color(0xFFFFFFFF),
    val muted: Color = Color(0xFF7C8291),
    val border: Color = Color(0xFFE7E9F0),
    val lime: Color = Color(0xFFC8FF45),
    val warmBrown: Color = Color(0xFF7A4D34),
    val denim: Color = Color(0xFF456A9E),
    val cream: Color = Color(0xFFF1E4C8),
)

fun darkAlmiColors() = AlmiColors(
    ink = Color(0xFFF5F7FF),
    cobalt = Color(0xFF9CB4FF),
    cobaltPressed = Color(0xFFB4C5FF),
    cobaltSoft = Color(0xFF24345D),
    cloud = Color(0xFF20283A),
    white = Color(0xFF131B2E),
    muted = Color(0xFFAEB7CC),
    border = Color(0xFF35405A),
    lime = Color(0xFFC8FF45),
    warmBrown = Color(0xFFB98568),
    denim = Color(0xFF7695C4),
    cream = Color(0xFFE7D8B9),
)
