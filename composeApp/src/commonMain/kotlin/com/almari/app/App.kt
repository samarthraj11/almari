package com.almari.app

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalUriHandler
import com.almari.core.designsystem.AlmariTheme
import com.almari.core.designsystem.LocalAlmariColors
import com.almari.core.designsystem.LocalAlmariSpacing
import com.almari.core.designsystem.LocalAlmariTypography
import com.almari.core.designsystem.components.AlmariLogo
import com.almari.feature.home.HomeScreen
import com.almari.feature.home.OnboardingScreen
import com.almari.shared.feature.home.DefaultHomeComponent
import com.almari.shared.data.createAlmariApi
import com.almari.shared.data.defaultGoogleAuthStartUrl
import kotlinx.coroutines.delay

@Composable
fun App() {
    val homeComponent = remember { DefaultHomeComponent(createAlmariApi()) }
    val homeState by homeComponent.state.collectAsState()
    val uriHandler = LocalUriHandler.current
    AlmariTheme(darkTheme = homeState.profile.isDarkMode) {
        var showSplash by remember { mutableStateOf(true) }

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
                AlmariSplashScreen()
            } else if (!homeState.isConnected) {
                OnboardingScreen(
                    isLoading = homeState.isSyncing,
                    error = homeState.authError,
                    onContinueWithGoogle = { uriHandler.openUri(defaultGoogleAuthStartUrl) },
                )
            } else {
                HomeScreen(component = homeComponent)
            }
        }
    }
}

@Composable
private fun AlmariSplashScreen(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        LocalAlmariColors.current.white,
                        LocalAlmariColors.current.cobaltSoft,
                    ),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            AlmariLogo(size = 88.dp)
            Spacer(Modifier.height(LocalAlmariSpacing.current.md))
            Text(
                text = "ALMARI.",
                color = LocalAlmariColors.current.ink,
                style = LocalAlmariTypography.current.display,
            )
            Spacer(Modifier.height(LocalAlmariSpacing.current.xs))
            Text(
                text = "YOUR CLOSET. IN PLAY.",
                color = LocalAlmariColors.current.cobalt,
                style = LocalAlmariTypography.current.eyebrow,
            )
        }
    }
}

private const val SPLASH_DURATION_MILLIS = 950L
private const val CROSSFADE_DURATION_MILLIS = 280
