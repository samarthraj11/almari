package com.almari.core.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.almari.core.designsystem.theme.AlmariColors
import com.almari.core.designsystem.theme.AlmariSizes
import com.almari.core.designsystem.theme.AlmariSpacing
import com.almari.core.designsystem.theme.AlmariTypography
import com.almari.core.designsystem.theme.darkAlmariColors

val LocalAlmariColors = staticCompositionLocalOf { AlmariColors() }
val LocalAlmariTypography = staticCompositionLocalOf { AlmariTypography() }
val LocalAlmariSpacing = staticCompositionLocalOf { AlmariSpacing() }
val LocalAlmariSizes = staticCompositionLocalOf { AlmariSizes() }

@Composable
fun AlmariTheme(darkTheme: Boolean = false, content: @Composable () -> Unit) {
    val colors = if (darkTheme) darkAlmariColors() else AlmariColors()
    CompositionLocalProvider(
        LocalAlmariColors provides colors,
        LocalAlmariTypography provides AlmariTypography(),
        LocalAlmariSpacing provides AlmariSpacing(),
        LocalAlmariSizes provides AlmariSizes(),
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
