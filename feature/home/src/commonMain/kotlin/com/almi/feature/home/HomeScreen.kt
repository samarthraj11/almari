package com.almi.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material.icons.rounded.Casino
import androidx.compose.material.icons.rounded.Checkroom
import androidx.compose.material.icons.rounded.PersonOutline
import androidx.compose.material.icons.rounded.PhotoCamera
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.almi.core.designsystem.LocalAlmiColors
import com.almi.core.designsystem.LocalAlmiSizes
import com.almi.core.designsystem.LocalAlmiSpacing
import com.almi.core.designsystem.LocalAlmiTypography
import com.almi.core.designsystem.components.AlmiLogo
import com.almi.feature.home.components.WardrobeRailCard
import com.almi.shared.feature.home.HomeComponent
import com.almi.shared.feature.home.HomeTab
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    component: HomeComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.state.collectAsState()
    val colors = LocalAlmiColors.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.white)
            .statusBarsPadding(),
    ) {
        Column(Modifier.fillMaxSize()) {
            if (state.selectedTab == HomeTab.Outfit) HomeHeader(credits = state.credits, onOpenPreferences = { component.selectTab(HomeTab.Profile) }) else BrandBar(state.credits, state.profile.name)
            Box(Modifier.weight(1f).fillMaxWidth()) {
                when (state.selectedTab) {
                    HomeTab.Outfit -> Column(Modifier.fillMaxSize()) {
                        LazyColumn(
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = LocalAlmiSpacing.current.md, vertical = LocalAlmiSpacing.current.xs),
                            verticalArrangement = Arrangement.spacedBy(LocalAlmiSpacing.current.xs),
                        ) {
                            items(state.rails, key = { it.slot.name }) { rail -> WardrobeRailCard(rail, { component.move(rail.slot, -1) }, { component.move(rail.slot, 1) }, { component.toggleLock(rail.slot) }) }
                        }
                        Text(
                            text = "SAVE THIS LOOK",
                            color = LocalAlmiColors.current.cobalt,
                            style = LocalAlmiTypography.current.button,
                            modifier = Modifier.fillMaxWidth().clickable { component.saveCurrentOutfit("Almi Mix ${state.savedOutfits.size + 1}") }.padding(vertical = 8.dp),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        )
                        ShuffleBar(onShuffle = component::shuffle)
                    }
                    HomeTab.Closet -> ClosetScreen(state, component)
                    HomeTab.Capture -> ClosetScreen(state, component)
                    HomeTab.Saved -> SavedScreen(state, component)
                    HomeTab.Profile -> ProfileScreen(state, component)
                }
            }
            BottomNavigation(selectedTab = state.selectedTab, onSelectTab = component::selectTab)
        }
        state.message?.let { message ->
            LaunchedEffect(message) { delay(1800); component.clearMessage() }
            Text(message, color = colors.white, style = LocalAlmiTypography.current.body, modifier = Modifier.align(Alignment.TopCenter).padding(top = 54.dp).background(colors.ink, CircleShape).padding(horizontal = 16.dp, vertical = 10.dp))
        }
    }
}

@Composable
private fun BrandBar(credits: Int, name: String) {
    Row(Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 20.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AlmiLogo(size = 30.dp); Spacer(Modifier.width(7.dp)); Text("ALMI.", style = LocalAlmiTypography.current.brand)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("$credits CREDITS", color = LocalAlmiColors.current.cobalt, style = LocalAlmiTypography.current.caption, modifier = Modifier.background(LocalAlmiColors.current.cobaltSoft, CircleShape).padding(horizontal = 10.dp, vertical = 6.dp))
            Spacer(Modifier.width(8.dp)); Box(Modifier.size(30.dp).background(LocalAlmiColors.current.ink, CircleShape), Alignment.Center) { Text(name.take(2).uppercase(), color = LocalAlmiColors.current.lime, style = LocalAlmiTypography.current.caption) }
        }
    }
}

@Composable
private fun HomeHeader(
    credits: Int,
    onOpenPreferences: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = LocalAlmiSpacing.current.lg, vertical = LocalAlmiSpacing.current.xs),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AlmiLogo(size = LocalAlmiSizes.current.logo)
                Spacer(Modifier.width(LocalAlmiSpacing.current.xs))
                Text(
                    text = buildAnnotatedString {
                        append("ALM")
                        withStyle(androidx.compose.ui.text.SpanStyle(color = LocalAlmiColors.current.cobalt)) {
                            append("I")
                        }
                        append(".")
                    },
                    color = LocalAlmiColors.current.ink,
                    style = LocalAlmiTypography.current.brand,
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    modifier = Modifier
                        .background(LocalAlmiColors.current.cobaltSoft, CircleShape)
                        .padding(horizontal = LocalAlmiSpacing.current.sm, vertical = LocalAlmiSpacing.current.xs),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        Modifier
                            .size(LocalAlmiSpacing.current.xs)
                            .background(LocalAlmiColors.current.cobalt, CircleShape),
                    )
                    Spacer(Modifier.width(LocalAlmiSpacing.current.xs))
                    Text(
                        text = "$credits CREDITS",
                        color = LocalAlmiColors.current.cobalt,
                        style = LocalAlmiTypography.current.caption,
                    )
                }
                Spacer(Modifier.width(LocalAlmiSpacing.current.xs))
                Box(
                    modifier = Modifier
                        .size(LocalAlmiSizes.current.logo)
                        .background(LocalAlmiColors.current.ink, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "SK",
                        color = LocalAlmiColors.current.lime,
                        style = LocalAlmiTypography.current.caption,
                    )
                }
            }
        }
        Spacer(Modifier.height(LocalAlmiSpacing.current.md))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = "AI STYLIST MIXER",
                    color = LocalAlmiColors.current.cobalt,
                    style = LocalAlmiTypography.current.eyebrow,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "OUTFIT STUDIO",
                        color = LocalAlmiColors.current.ink,
                        style = LocalAlmiTypography.current.display,
                    )
                    Spacer(Modifier.width(LocalAlmiSpacing.current.xs))
                    Icon(
                        Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = LocalAlmiColors.current.cobalt,
                        modifier = Modifier.size(LocalAlmiSizes.current.iconMedium),
                    )
                }
            }
            IconButton(
                onClick = onOpenPreferences,
                modifier = Modifier
                    .size(LocalAlmiSizes.current.tapTarget)
                    .background(LocalAlmiColors.current.cloud, CircleShape),
            ) {
                Icon(
                    Icons.Rounded.Tune,
                    contentDescription = "Outfit studio settings",
                    tint = LocalAlmiColors.current.ink,
                )
            }
        }
    }
}

@Composable
private fun ShuffleBar(
    onShuffle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = LocalAlmiSpacing.current.md, vertical = LocalAlmiSpacing.current.xs),
        horizontalArrangement = Arrangement.spacedBy(LocalAlmiSpacing.current.xs),
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .background(LocalAlmiColors.current.cloud, RoundedCornerShape(LocalAlmiSizes.current.buttonRadius))
                .clickable(role = Role.Button, onClick = onShuffle)
                .semantics { contentDescription = "Surprise me with a random outfit" },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Rounded.Casino,
                contentDescription = null,
                tint = LocalAlmiColors.current.ink,
            )
        }
        Row(
            modifier = Modifier
                .weight(1f)
                .height(54.dp)
                .shadow(10.dp, RoundedCornerShape(LocalAlmiSizes.current.buttonRadius))
                .background(LocalAlmiColors.current.cobalt, RoundedCornerShape(LocalAlmiSizes.current.buttonRadius))
                .clickable(role = Role.Button, onClick = onShuffle),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                Icons.Rounded.Shuffle,
                contentDescription = null,
                tint = LocalAlmiColors.current.cobaltSoft,
                modifier = Modifier.size(LocalAlmiSizes.current.iconMedium),
            )
            Spacer(Modifier.width(LocalAlmiSpacing.current.xs))
            Text(
                text = "SHUFFLE LOOK",
                color = LocalAlmiColors.current.white,
                style = LocalAlmiTypography.current.button,
            )
            Spacer(Modifier.width(LocalAlmiSpacing.current.xs))
            Icon(
                Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint = LocalAlmiColors.current.lime,
                modifier = Modifier.size(LocalAlmiSizes.current.iconSmall),
            )
        }
    }
}

@Composable
private fun BottomNavigation(
    selectedTab: HomeTab,
    onSelectTab: (HomeTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(LocalAlmiColors.current.white)
            .navigationBarsPadding()
            .padding(horizontal = LocalAlmiSpacing.current.sm, vertical = LocalAlmiSpacing.current.xs),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        NavigationItem(HomeTab.Outfit, Icons.Rounded.AutoAwesome, selectedTab, onSelectTab)
        NavigationItem(HomeTab.Closet, Icons.Rounded.Checkroom, selectedTab, onSelectTab)
        CaptureItem(onClick = { onSelectTab(HomeTab.Capture) })
        NavigationItem(HomeTab.Saved, Icons.Rounded.BookmarkBorder, selectedTab, onSelectTab)
        NavigationItem(HomeTab.Profile, Icons.Rounded.PersonOutline, selectedTab, onSelectTab)
    }
}

@Composable
private fun NavigationItem(
    tab: HomeTab,
    icon: ImageVector,
    selectedTab: HomeTab,
    onSelectTab: (HomeTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selected = tab == selectedTab
    Column(
        modifier = modifier
            .clickable(role = Role.Tab) { onSelectTab(tab) }
            .padding(horizontal = LocalAlmiSpacing.current.xs, vertical = LocalAlmiSpacing.current.xxs),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = tab.label.lowercase(),
            tint = if (selected) LocalAlmiColors.current.cobalt else LocalAlmiColors.current.muted,
            modifier = Modifier.size(LocalAlmiSizes.current.iconMedium),
        )
        Text(
            text = tab.label,
            color = if (selected) LocalAlmiColors.current.cobalt else LocalAlmiColors.current.muted,
            style = LocalAlmiTypography.current.caption,
        )
        Box(
            Modifier
                .padding(top = LocalAlmiSpacing.current.xxs)
                .size(LocalAlmiSpacing.current.xxs)
                .background(if (selected) LocalAlmiColors.current.cobalt else Color.Transparent, CircleShape),
        )
    }
}

@Composable
private fun CaptureItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(52.dp)
            .shadow(10.dp, CircleShape)
            .background(LocalAlmiColors.current.cobalt, CircleShape)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            Icons.Rounded.PhotoCamera,
            contentDescription = "Capture a garment",
            tint = LocalAlmiColors.current.white,
            modifier = Modifier.size(LocalAlmiSizes.current.iconLarge),
        )
    }
}
