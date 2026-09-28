package com.almari.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class AlmariColors(
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
    val studioBackground: Color = Color(0xFFFFFBF6),
    val studioInk: Color = Color(0xFF111111),
    val studioMuted: Color = Color(0xFF6D6A70),
    val studioStroke: Color = Color(0xFFECE7E1),
    val acidLime: Color = Color(0xFFD9FF32),
    val hotPink: Color = Color(0xFFFF4FCB),
    val softPink: Color = Color(0xFFFFDDEB),
    val softLilac: Color = Color(0xFFE9DEFF),
    val electricBlue: Color = Color(0xFF5B6CFF),
    val skyTint: Color = Color(0xFFDDF4FF),
    val mintTint: Color = Color(0xFFDEF8D7),
    val butterTint: Color = Color(0xFFFFF4B8),
    val peachTint: Color = Color(0xFFFFE6D6),
)

fun darkAlmariColors() = AlmariColors(
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
