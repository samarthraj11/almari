package com.almari.feature.home

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
import com.almari.core.designsystem.LocalAlmariColors
import com.almari.core.designsystem.LocalAlmariSizes
import com.almari.core.designsystem.LocalAlmariSpacing
import com.almari.core.designsystem.LocalAlmariTypography
import com.almari.core.designsystem.components.AlmariLogo
import com.almari.feature.home.components.WardrobeRailCard
import com.almari.shared.feature.home.HomeComponent
import com.almari.shared.feature.home.HomeTab
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    component: HomeComponent,
    modifier: Modifier = Modifier,
) {
    val state by component.state.collectAsState()
    val colors = LocalAlmariColors.current

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
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = LocalAlmariSpacing.current.md, vertical = LocalAlmariSpacing.current.xs),
                            verticalArrangement = Arrangement.spacedBy(LocalAlmariSpacing.current.xs),
                        ) {
                            items(state.rails, key = { it.slot.name }) { rail -> WardrobeRailCard(rail, { component.move(rail.slot, -1) }, { component.move(rail.slot, 1) }, { component.toggleLock(rail.slot) }) }
                        }
                        Text(
                            text = "SAVE THIS LOOK",
                            color = LocalAlmariColors.current.cobalt,
                            style = LocalAlmariTypography.current.button,
                            modifier = Modifier.fillMaxWidth().clickable { component.saveCurrentOutfit("Almari Mix ${state.savedOutfits.size + 1}") }.padding(vertical = 8.dp),
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
            Text(message, color = colors.white, style = LocalAlmariTypography.current.body, modifier = Modifier.align(Alignment.TopCenter).padding(top = 54.dp).background(colors.ink, CircleShape).padding(horizontal = 16.dp, vertical = 10.dp))
        }
    }
}

@Composable
private fun BrandBar(credits: Int, name: String) {
    Row(Modifier.fillMaxWidth().height(56.dp).padding(horizontal = 20.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AlmariLogo(size = 30.dp); Spacer(Modifier.width(7.dp)); Text("ALMARI.", style = LocalAlmariTypography.current.brand)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("$credits  CREDITS", color = LocalAlmariColors.current.cobalt, style = LocalAlmariTypography.current.caption, modifier = Modifier.background(LocalAlmariColors.current.cobaltSoft, CircleShape).padding(horizontal = 10.dp, vertical = 6.dp))
            Spacer(Modifier.width(8.dp)); Box(Modifier.size(30.dp).background(LocalAlmariColors.current.ink, CircleShape), Alignment.Center) { Text(name.take(2).uppercase(), color = LocalAlmariColors.current.lime, style = LocalAlmariTypography.current.caption) }
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
            .padding(horizontal = LocalAlmariSpacing.current.lg, vertical = LocalAlmariSpacing.current.xs),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AlmariLogo(size = LocalAlmariSizes.current.logo)
                Spacer(Modifier.width(LocalAlmariSpacing.current.xs))
                Text(
                    text = buildAnnotatedString {
                        append("ALM")
                        withStyle(androidx.compose.ui.text.SpanStyle(color = LocalAlmariColors.current.cobalt)) {
                            append("I")
                        }
                        append(".")
                    },
                    color = LocalAlmariColors.current.ink,
                    style = LocalAlmariTypography.current.brand,
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    modifier = Modifier
                        .background(LocalAlmariColors.current.cobaltSoft, CircleShape)
                        .padding(horizontal = LocalAlmariSpacing.current.sm, vertical = LocalAlmariSpacing.current.xs),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        Modifier
                            .size(LocalAlmariSpacing.current.xs)
                            .background(LocalAlmariColors.current.cobalt, CircleShape),
                    )
                    Spacer(Modifier.width(LocalAlmariSpacing.current.xs))
                    Text(
                        text = "$credits CREDITS",
                        color = LocalAlmariColors.current.cobalt,
                        style = LocalAlmariTypography.current.caption,
                    )
                }
                Spacer(Modifier.width(LocalAlmariSpacing.current.xs))
                Box(
                    modifier = Modifier
                        .size(LocalAlmariSizes.current.logo)
                        .background(LocalAlmariColors.current.ink, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "SK",
                        color = LocalAlmariColors.current.lime,
                        style = LocalAlmariTypography.current.caption,
                    )
                }
            }
        }
        Spacer(Modifier.height(LocalAlmariSpacing.current.md))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                Text(
                    text = "AI STYLIST MIXER",
                    color = LocalAlmariColors.current.cobalt,
                    style = LocalAlmariTypography.current.eyebrow,
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "OUTFIT STUDIO",
                        color = LocalAlmariColors.current.ink,
                        style = LocalAlmariTypography.current.display,
                    )
                    Spacer(Modifier.width(LocalAlmariSpacing.current.xs))
                    Icon(
                        Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        tint = LocalAlmariColors.current.cobalt,
                        modifier = Modifier.size(LocalAlmariSizes.current.iconMedium),
                    )
                }
            }
            IconButton(
                onClick = onOpenPreferences,
                modifier = Modifier
                    .size(LocalAlmariSizes.current.tapTarget)
                    .background(LocalAlmariColors.current.cloud, CircleShape),
            ) {
                Icon(
                    Icons.Rounded.Tune,
                    contentDescription = "Outfit studio settings",
                    tint = LocalAlmariColors.current.ink,
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
            .padding(horizontal = LocalAlmariSpacing.current.md, vertical = LocalAlmariSpacing.current.xs),
        horizontalArrangement = Arrangement.spacedBy(LocalAlmariSpacing.current.xs),
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .background(LocalAlmariColors.current.cloud, RoundedCornerShape(LocalAlmariSizes.current.buttonRadius))
                .clickable(role = Role.Button, onClick = onShuffle)
                .semantics { contentDescription = "Surprise me with a random outfit" },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Rounded.Casino,
                contentDescription = null,
                tint = LocalAlmariColors.current.ink,
            )
        }
        Row(
            modifier = Modifier
                .weight(1f)
                .height(54.dp)
                .shadow(10.dp, RoundedCornerShape(LocalAlmariSizes.current.buttonRadius))
                .background(LocalAlmariColors.current.cobalt, RoundedCornerShape(LocalAlmariSizes.current.buttonRadius))
                .clickable(role = Role.Button, onClick = onShuffle),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Icon(
                Icons.Rounded.Shuffle,
                contentDescription = null,
                tint = LocalAlmariColors.current.cobaltSoft,
                modifier = Modifier.size(LocalAlmariSizes.current.iconMedium),
            )
            Spacer(Modifier.width(LocalAlmariSpacing.current.xs))
            Text(
                text = "SHUFFLE LOOK",
                color = LocalAlmariColors.current.white,
                style = LocalAlmariTypography.current.button,
            )
            Spacer(Modifier.width(LocalAlmariSpacing.current.xs))
            Icon(
                Icons.Rounded.AutoAwesome,
                contentDescription = null,
                tint = LocalAlmariColors.current.lime,
                modifier = Modifier.size(LocalAlmariSizes.current.iconSmall),
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
            .background(LocalAlmariColors.current.white)
            .navigationBarsPadding()
            .padding(horizontal = LocalAlmariSpacing.current.sm, vertical = LocalAlmariSpacing.current.xs),
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
            .padding(horizontal = LocalAlmariSpacing.current.xs, vertical = LocalAlmariSpacing.current.xxs),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = tab.label.lowercase(),
            tint = if (selected) LocalAlmariColors.current.cobalt else LocalAlmariColors.current.muted,
            modifier = Modifier.size(LocalAlmariSizes.current.iconMedium),
        )
        Text(
            text = tab.label,
            color = if (selected) LocalAlmariColors.current.cobalt else LocalAlmariColors.current.muted,
            style = LocalAlmariTypography.current.caption,
        )
        Box(
            Modifier
                .padding(top = LocalAlmariSpacing.current.xxs)
                .size(LocalAlmariSpacing.current.xxs)
                .background(if (selected) LocalAlmariColors.current.cobalt else Color.Transparent, CircleShape),
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
            .background(LocalAlmariColors.current.cobalt, CircleShape)
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            Icons.Rounded.PhotoCamera,
            contentDescription = "Capture a garment",
            tint = LocalAlmariColors.current.white,
            modifier = Modifier.size(LocalAlmariSizes.current.iconLarge),
        )
    }
}
