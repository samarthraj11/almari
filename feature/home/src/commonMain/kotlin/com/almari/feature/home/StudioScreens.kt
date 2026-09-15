package com.almari.feature.home

import almari.feature.home.generated.resources.Res
import almari.feature.home.generated.resources.garment_black_bag
import almari.feature.home.generated.resources.garment_pink_bag
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.almari.core.designsystem.LocalAlmariColors
import com.almari.feature.home.components.NeonAction
import com.almari.feature.home.components.StudioChip
import com.almari.feature.home.components.StudioGarmentVisual
import com.almari.feature.home.components.WardrobeRailCard
import com.almari.shared.feature.home.ClosetFilter
import com.almari.shared.feature.home.HomeComponent
import com.almari.shared.feature.home.HomeState
import com.almari.shared.feature.home.HomeTab
import com.almari.shared.feature.home.SavedFilter
import com.almari.shared.feature.home.SavedOutfit
import com.almari.shared.feature.home.WardrobeSlot
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun OutfitStudioScreen(state: HomeState, component: HomeComponent) {
    val colors = LocalAlmariColors.current
    Column(Modifier.fillMaxSize().padding(horizontal = 14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "OUTFIT\nSTUDIO",
                    color = colors.studioInk,
                    fontSize = 27.sp,
                    lineHeight = 23.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1).sp,
                )
                Icon(
                    Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    tint = colors.electricBlue,
                    modifier = Modifier.padding(start = 7.dp).size(28.dp),
                )
            }
            IconButton(onClick = { component.selectTab(HomeTab.Profile) }) {
                Icon(Icons.Rounded.Settings, "Open profile and settings", tint = colors.studioInk)
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(7.dp),
            contentPadding = PaddingValues(bottom = 8.dp),
        ) {
            items(state.rails, key = { it.slot.name }) { rail ->
                WardrobeRailCard(
                    rail = rail,
                    onPrevious = { component.move(rail.slot, -1) },
                    onNext = { component.move(rail.slot, 1) },
                    onToggleLock = { component.toggleLock(rail.slot) },
                    modifier = Modifier.height(
                        if (rail.slot == WardrobeSlot.Head || rail.slot == WardrobeSlot.Shoes) 84.dp else 104.dp,
                    ),
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 9.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(Color.White, RoundedCornerShape(27.dp))
                    .border(1.dp, colors.studioStroke, RoundedCornerShape(27.dp))
                    .clickable { component.saveCurrentOutfit("New fit " + (state.savedOutfits.size + 1)) },
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.Rounded.Save, "Save this fit", tint = colors.studioInk)
            }
            NeonAction(label = "SHUFFLE", onClick = component::shuffle, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
internal fun WardrobeGridScreen(state: HomeState, component: HomeComponent) {
    val colors = LocalAlmariColors.current
    var searchVisible by remember { mutableStateOf(state.closetQuery.isNotBlank()) }
    val visible = state.garments.filter { item ->
        val matchesText = state.closetQuery.isBlank() ||
            item.name.contains(state.closetQuery, true) ||
            item.brand.contains(state.closetQuery, true)
        val slot = state.rails.firstOrNull { rail -> rail.items.any { it.id == item.id } }?.slot
        val matchesFilter = when (state.closetFilter) {
            ClosetFilter.All -> true
            ClosetFilter.Tops -> slot == WardrobeSlot.Top
            ClosetFilter.Layers -> slot == WardrobeSlot.Layer
            ClosetFilter.Bottoms -> slot == WardrobeSlot.Bottom
            ClosetFilter.Shoes -> slot == WardrobeSlot.Shoes
        }
        matchesText && matchesFilter
    }

    Column(Modifier.fillMaxSize().padding(horizontal = 15.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                "WARDROBE",
                color = colors.studioInk,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = (-1).sp,
            )
            Row {
                IconButton(onClick = { searchVisible = !searchVisible }) {
                    Icon(Icons.Rounded.Search, "Search wardrobe", tint = colors.studioInk)
                }
                IconButton(onClick = { component.selectTab(HomeTab.Capture) }) {
                    Icon(Icons.Rounded.CameraAlt, "Capture garments", tint = colors.studioInk)
                }
            }
        }

        if (searchVisible) {
            OutlinedTextField(
                value = state.closetQuery,
                onValueChange = component::setClosetQuery,
                modifier = Modifier.fillMaxWidth().padding(bottom = 9.dp),
                placeholder = { Text("Search " + state.garments.size + " pieces") },
                leadingIcon = { Icon(Icons.Rounded.Search, null) },
                singleLine = true,
                shape = RoundedCornerShape(18.dp),
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            ClosetFilter.entries.forEach { filter ->
                StudioChip(
                    label = filter.label.lowercase().replaceFirstChar { it.uppercase() },
                    selected = state.closetFilter == filter,
                    onClick = { component.setClosetFilter(filter) },
                )
            }
        }

        if (visible.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No pieces match this view.", color = colors.studioMuted)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 5.dp, bottom = 18.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(visible, key = { it.id }) { item ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(0.78f)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { component.showGarment(item.id) },
                        contentAlignment = Alignment.Center,
                    ) {
                        StudioGarmentVisual(item, Modifier.fillMaxSize())
                    }
                }
            }
        }
    }
    state.garments.firstOrNull { it.id == state.selectedGarmentId }?.let {
        GarmentDetailSheet(it, component)
    }
}

@Composable
internal fun SavedFitsScreen(state: HomeState, component: HomeComponent) {
    val colors = LocalAlmariColors.current
    val outfits = state.savedOutfits.filter { outfit ->
        when (state.savedFilter) {
            SavedFilter.All -> true
            SavedFilter.Worn -> outfit.lastWorn != "Not worn yet"
            SavedFilter.Favorites -> outfit.isFavorite
        }
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 15.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 16.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "SAVED FITS",
                    color = colors.studioInk,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1).sp,
                )
                IconButton(
                    onClick = { component.selectTab(HomeTab.Outfit) },
                    modifier = Modifier.size(40.dp).background(colors.acidLime, CircleShape),
                ) {
                    Icon(Icons.Rounded.AutoAwesome, "Create a new fit", tint = colors.studioInk)
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                SavedFilter.entries.forEach { filter ->
                    StudioChip(filter.label, state.savedFilter == filter) {
                        component.setSavedFilter(filter)
                    }
                }
            }
        }
        if (outfits.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().height(260.dp), contentAlignment = Alignment.Center) {
                    Text("No fits here yet. Save one from Outfit Studio.", color = colors.studioMuted)
                }
            }
        } else {
            items(outfits, key = { it.id }) { outfit ->
                SavedFitCard(outfit, component)
            }
        }
    }

    state.savedOutfits.firstOrNull { it.id == state.selectedOutfitId }?.let { outfit ->
        SavedFitDialog(outfit, component)
    }
}

@Composable
private fun SavedFitCard(outfit: SavedOutfit, component: HomeComponent) {
    val colors = LocalAlmariColors.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(174.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Brush.horizontalGradient(listOf(colors.softPink, colors.skyTint)))
            .clickable { component.showOutfit(outfit.id) }
            .padding(11.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(142.dp)
                .fillMaxHeight()
                .background(Color.White.copy(alpha = 0.38f), RoundedCornerShape(17.dp)),
            contentAlignment = Alignment.Center,
        ) {
            outfit.items.take(3).forEachIndexed { index, item ->
                StudioGarmentVisual(
                    item = item,
                    modifier = Modifier
                        .align(
                            when (index) {
                                0 -> Alignment.TopCenter
                                1 -> Alignment.BottomStart
                                else -> Alignment.BottomEnd
                            },
                        )
                        .size(if (index == 0) 105.dp else 64.dp),
                )
            }
            Image(
                painter = painterResource(
                    if (outfit.id.hashCode() % 2 == 0) Res.drawable.garment_pink_bag else Res.drawable.garment_black_bag,
                ),
                contentDescription = null,
                modifier = Modifier.align(Alignment.BottomEnd).size(55.dp),
                contentScale = ContentScale.Fit,
            )
        }
        Column(Modifier.padding(start = 13.dp).weight(1f).fillMaxHeight()) {
            Text(
                outfit.name,
                color = colors.studioInk,
                fontSize = 18.sp,
                lineHeight = 20.sp,
                fontWeight = FontWeight.Black,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(5.dp))
            Text(
                if (outfit.lastWorn == "Not worn yet") outfit.lastWorn else "Worn " + outfit.lastWorn,
                color = colors.studioMuted,
                fontSize = 12.sp,
            )
            Spacer(Modifier.weight(1f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = { component.toggleFavorite(outfit.id) }) {
                    Icon(
                        if (outfit.isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                        "Favorite fit",
                        tint = if (outfit.isFavorite) colors.hotPink else colors.studioInk,
                    )
                }
                IconButton(onClick = { component.showOutfit(outfit.id) }) {
                    Icon(Icons.Rounded.MoreHoriz, "Fit options", tint = colors.studioInk)
                }
            }
        }
    }
}

@Composable
private fun SavedFitDialog(outfit: SavedOutfit, component: HomeComponent) {
    val colors = LocalAlmariColors.current
    AlertDialog(
        onDismissRequest = { component.showOutfit(null) },
        containerColor = colors.studioBackground,
        title = { Text(outfit.name, fontWeight = FontWeight.Black) },
        text = { Text(outfit.items.size.toString() + " pieces · " + outfit.occasion + "\nLast worn: " + outfit.lastWorn) },
        confirmButton = {
            TextButton(onClick = { component.editOutfit(outfit.id) }) {
                Icon(Icons.Rounded.Edit, null)
                Spacer(Modifier.width(5.dp))
                Text("EDIT IN STUDIO")
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = { component.deleteOutfit(outfit.id) }) {
                    Icon(Icons.Rounded.Delete, null, tint = Color(0xFFDC2626))
                    Spacer(Modifier.width(4.dp))
                    Text("DELETE", color = Color(0xFFDC2626))
                }
                IconButton(onClick = { component.showOutfit(null) }) {
                    Icon(Icons.Rounded.Close, "Close")
                }
            }
        },
    )
}
