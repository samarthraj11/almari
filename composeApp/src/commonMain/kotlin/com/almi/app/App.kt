package com.almi.app

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.almi.core.designsystem.AlmiTheme
import com.almi.core.designsystem.LocalAlmiColors
import com.almi.core.designsystem.LocalAlmiSpacing
import com.almi.core.designsystem.LocalAlmiTypography
import com.almi.core.designsystem.components.AlmiLogo
import com.almi.feature.home.HomeScreen
import com.almi.shared.feature.home.DefaultHomeComponent
import kotlinx.coroutines.delay

@Composable
fun App() {
    AlmiTheme {
        var showSplash by remember { mutableStateOf(true) }
        val homeComponent = remember { DefaultHomeComponent() }

        LaunchedEffect(Unit) {
            delay(SPLASH_DURATION_MILLIS)
            showSplash = false
        }

        Crossfade(
            targetState = showSplash,
            animationSpec = tween(CROSSFADE_DURATION_MILLIS),
            label = "splash-to-home",
        ) { isSplashVisible ->
            if (isSplashVisible) {
                AlmiSplashScreen()
            } else {
                HomeScreen(component = homeComponent)
            }
        }
    }
}

@Composable
private fun AlmiSplashScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        LocalAlmiColors.current.white,
                        LocalAlmiColors.current.cobaltSoft,
                    ),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            AlmiLogo(size = 88.dp)
            Spacer(Modifier.height(LocalAlmiSpacing.current.md))
            Text(
                text = "ALMI.",
                color = LocalAlmiColors.current.ink,
                style = LocalAlmiTypography.current.display,
            )
            Spacer(Modifier.height(LocalAlmiSpacing.current.xs))
            Text(
                text = "YOUR CLOSET. IN PLAY.",
                color = LocalAlmiColors.current.cobalt,
                style = LocalAlmiTypography.current.eyebrow,
            )
        }
    }
}

private const val SPLASH_DURATION_MILLIS = 950L
private const val CROSSFADE_DURATION_MILLIS = 280
