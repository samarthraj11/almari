package com.almari.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.PersonOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.almari.core.designsystem.LocalAlmariColors
import com.almari.core.designsystem.LocalAlmariTypography
import com.almari.shared.feature.home.HomeComponent
import com.almari.shared.feature.home.HomeTab
import com.almari.shared.feature.home.TryOnStage
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(component: HomeComponent, modifier: Modifier = Modifier) {
    val state by component.state.collectAsState()
    val colors = LocalAlmariColors.current
    val isCaptureFlow = state.selectedTab == HomeTab.Capture
    val isTryOnFlow = state.selectedTab == HomeTab.Outfit && state.tryOnStage != TryOnStage.Hidden
    val isImmersiveFlow = isCaptureFlow || isTryOnFlow

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.studioBackground)
            .then(if (isImmersiveFlow) Modifier else Modifier.statusBarsPadding()),
    ) {
        Column(Modifier.fillMaxSize()) {
            Box(Modifier.weight(1f).fillMaxWidth()) {
                when (state.selectedTab) {
                    HomeTab.Outfit -> if (isTryOnFlow) AiTryOnScreen(state, component) else OutfitStudioScreen(state, component)
                    HomeTab.Closet -> WardrobeGridScreen(state, component)
                    HomeTab.Capture -> CaptureFlowScreen(component)
                    HomeTab.Saved -> SavedFitsScreen(state, component)
                    HomeTab.Profile -> Box(Modifier.fillMaxSize().background(colors.white)) {
                        ProfileScreen(state, component)
                    }
                }
            }
            if (!isImmersiveFlow) {
                StudioBottomNavigation(
                    selectedTab = state.selectedTab,
                    onSelectTab = component::selectTab,
                )
            }
        }

        state.message?.let { message ->
            LaunchedEffect(message) {
                delay(1800)
                component.clearMessage()
            }
            Text(
                text = message,
                color = Color.White,
                style = LocalAlmariTypography.current.body,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
                    .padding(top = 10.dp)
                    .background(colors.studioInk, CircleShape)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
            )
        }
    }
}

@Composable
private fun StudioBottomNavigation(
    selectedTab: HomeTab,
    onSelectTab: (HomeTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalAlmariColors.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .navigationBarsPadding()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StudioNavigationItem(HomeTab.Outfit, Icons.Rounded.Home, selectedTab, onSelectTab)
        StudioNavigationItem(HomeTab.Closet, Icons.Rounded.Checkroom, selectedTab, onSelectTab)
        Box(
            modifier = Modifier
                .offset(y = (-13).dp)
                .size(58.dp)
                .background(colors.acidLime, CircleShape)
                .border(5.dp, Color.White, CircleShape)
                .clickable(role = Role.Button) { onSelectTab(HomeTab.Capture) },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Rounded.Add,
                contentDescription = "Open camera to add a garment",
                tint = colors.studioInk,
                modifier = Modifier.size(30.dp),
            )
        }
        StudioNavigationItem(
            HomeTab.Saved,
            if (selectedTab == HomeTab.Saved) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
            selectedTab,
            onSelectTab,
        )
        StudioNavigationItem(HomeTab.Profile, Icons.Rounded.PersonOutline, selectedTab, onSelectTab)
    }
}

@Composable
private fun StudioNavigationItem(
    tab: HomeTab,
    icon: ImageVector,
    selectedTab: HomeTab,
    onSelectTab: (HomeTab) -> Unit,
) {
    val colors = LocalAlmariColors.current
    val selected = tab == selectedTab
    Column(
        modifier = Modifier
            .clickable(role = Role.Tab) { onSelectTab(tab) }
            .padding(horizontal = 18.dp, vertical = 3.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = tab.label.lowercase(),
            tint = colors.studioInk,
            modifier = Modifier.size(24.dp),
        )
        Box(
            Modifier
                .padding(top = 4.dp)
                .size(width = 25.dp, height = 4.dp)
                .background(if (selected) colors.acidLime else Color.Transparent, CircleShape),
        )
    }
}
