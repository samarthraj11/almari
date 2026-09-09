package com.almi.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.almi.core.designsystem.theme.AlmiColors
import com.almi.core.designsystem.theme.AlmiSizes
import com.almi.core.designsystem.theme.AlmiSpacing
import com.almi.core.designsystem.theme.AlmiTypography
import com.almi.core.designsystem.theme.darkAlmiColors

val LocalAlmiColors = staticCompositionLocalOf { AlmiColors() }
val LocalAlmiTypography = staticCompositionLocalOf { AlmiTypography() }
val LocalAlmiSpacing = staticCompositionLocalOf { AlmiSpacing() }
val LocalAlmiSizes = staticCompositionLocalOf { AlmiSizes() }

@Composable
fun AlmiTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    val colors = if (darkTheme) darkAlmiColors() else AlmiColors()
    CompositionLocalProvider(
        LocalAlmiColors provides colors,
        LocalAlmiTypography provides AlmiTypography(),
        LocalAlmiSpacing provides AlmiSpacing(),
        LocalAlmiSizes provides AlmiSizes(),
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) darkColorScheme(
                primary = colors.cobalt,
                onPrimary = colors.white,
                background = colors.white,
                onBackground = colors.ink,
                surface = colors.white,
                onSurface = colors.ink,
                outline = colors.border,
            ) else lightColorScheme(
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
