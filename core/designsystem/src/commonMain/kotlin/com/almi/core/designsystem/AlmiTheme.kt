package com.almi.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.almi.core.designsystem.theme.AlmiColors
import com.almi.core.designsystem.theme.AlmiSizes
import com.almi.core.designsystem.theme.AlmiSpacing
import com.almi.core.designsystem.theme.AlmiTypography

val LocalAlmiColors = staticCompositionLocalOf { AlmiColors() }
val LocalAlmiTypography = staticCompositionLocalOf { AlmiTypography() }
val LocalAlmiSpacing = staticCompositionLocalOf { AlmiSpacing() }
val LocalAlmiSizes = staticCompositionLocalOf { AlmiSizes() }

@Composable
fun AlmiTheme(content: @Composable () -> Unit) {
    val colors = AlmiColors()
    CompositionLocalProvider(
        LocalAlmiColors provides colors,
        LocalAlmiTypography provides AlmiTypography(),
        LocalAlmiSpacing provides AlmiSpacing(),
        LocalAlmiSizes provides AlmiSizes(),
    ) {
        MaterialTheme(
            colorScheme = lightColorScheme(
                primary = colors.cobalt,
                onPrimary = colors.white,
                background = colors.white,
                onBackground = colors.ink,
                surface = colors.white,
                onSurface = colors.ink,
                outline = colors.border,
            ),
            content = content,
        )
    }
}
