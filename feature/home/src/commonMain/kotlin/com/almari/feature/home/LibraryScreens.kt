package com.almari.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.HelpOutline
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.almari.core.designsystem.LocalAlmariColors
import com.almari.core.designsystem.LocalAlmariSpacing
import com.almari.core.designsystem.LocalAlmariTypography
import com.almari.feature.home.components.GarmentIllustration
import com.almari.shared.feature.home.*
import coil3.compose.AsyncImage
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openCameraPicker
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.extension
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.launch

@Composable
internal fun ClosetScreen(state: HomeState, component: HomeComponent, modifier: Modifier = Modifier) {
    val visible = state.garments.filter { item ->
        val matchesText = state.closetQuery.isBlank() || item.name.contains(state.closetQuery, true) || item.brand.contains(state.closetQuery, true)
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
    Column(modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Row(Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 10.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Text("WARDROBE", style = LocalAlmariTypography.current.display)
            Button(onClick = { component.showAddGarment(true) }, shape = CircleShape, contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
                Icon(Icons.Rounded.Add, null, Modifier.size(18.dp)); Spacer(Modifier.width(4.dp)); Text("ADD", style = LocalAlmariTypography.current.label)
            }
        }
        OutlinedTextField(
            value = state.closetQuery,
            onValueChange = component::setClosetQuery,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search ${state.garments.size} items…") },
            leadingIcon = { Icon(Icons.Rounded.Search, null) },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
        )
        Row(Modifier.fillMaxWidth().horizontalScroll(androidx.compose.foundation.rememberScrollState()).padding(vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ClosetFilter.entries.forEach { filter -> FilterChip(selected = state.closetFilter == filter, onClick = { component.setClosetFilter(filter) }, label = { Text(filter.label, style = LocalAlmariTypography.current.caption) }) }
        }
        Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), Arrangement.SpaceBetween) {
            Text("${visible.size} ITEMS", color = LocalAlmariColors.current.muted, style = LocalAlmariTypography.current.caption)
            Text("MOST WORN", style = LocalAlmariTypography.current.caption)
        }
        LazyVerticalGrid(columns = GridCells.Fixed(2), modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(visible, key = { it.id }) { item -> GarmentGridCard(item, onClick = { component.showGarment(item.id) }) }
        }
    }
    state.garments.firstOrNull { it.id == state.selectedGarmentId }?.let { GarmentDetailSheet(it, component) }
    if (state.isAddGarmentOpen) AddGarmentSheet(component)
}

@Composable
private fun GarmentGridCard(item: WardrobeItem, onClick: () -> Unit) {
    Column(Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(20.dp)).background(LocalAlmariColors.current.white, RoundedCornerShape(20.dp)).border(1.dp, LocalAlmariColors.current.border, RoundedCornerShape(20.dp)).clickable(onClick = onClick).padding(8.dp)) {
        Box(Modifier.fillMaxWidth().aspectRatio(.82f).background(LocalAlmariColors.current.cloud, RoundedCornerShape(15.dp)), Alignment.Center) {
            GarmentVisual(item, Modifier.fillMaxSize())
        }
        Row(Modifier.fillMaxWidth().padding(top = 8.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Text(item.brand.uppercase(), maxLines = 1, overflow = TextOverflow.Ellipsis, color = LocalAlmariColors.current.muted, style = LocalAlmariTypography.current.caption, modifier = Modifier.weight(1f))
            Text("${item.wearCount}x", color = LocalAlmariColors.current.cobalt, style = LocalAlmariTypography.current.caption, modifier = Modifier.background(LocalAlmariColors.current.cobaltSoft, CircleShape).padding(horizontal = 6.dp, vertical = 2.dp))
        }
        Text(item.name, maxLines = 1, overflow = TextOverflow.Ellipsis, style = LocalAlmariTypography.current.body)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GarmentDetailSheet(item: WardrobeItem, component: HomeComponent) {
    ModalBottomSheet(onDismissRequest = { component.showGarment(null) }, containerColor = LocalAlmariColors.current.white) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 22.dp).padding(bottom = 28.dp)) {
            Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                Text(item.styleTag, color = LocalAlmariColors.current.cobalt, style = LocalAlmariTypography.current.label)
                Text(item.brand, color = LocalAlmariColors.current.muted, style = LocalAlmariTypography.current.body)
            }
            Text(item.name, style = LocalAlmariTypography.current.display, modifier = Modifier.padding(top = 6.dp))
            Row(Modifier.fillMaxWidth().padding(vertical = 14.dp).background(LocalAlmariColors.current.cloud, RoundedCornerShape(14.dp)).padding(12.dp), Arrangement.SpaceBetween) {
                Text("🔥 ${item.wearCount} wears", style = LocalAlmariTypography.current.body)
                Text(item.lastWorn, color = LocalAlmariColors.current.muted, style = LocalAlmariTypography.current.caption)
            }
            Box(Modifier.fillMaxWidth().height(180.dp).background(LocalAlmariColors.current.cloud, RoundedCornerShape(18.dp)), Alignment.Center) { GarmentVisual(item, Modifier.fillMaxSize()) }
            Row(Modifier.fillMaxWidth().padding(top = 14.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(onClick = { component.styleGarment(item.id) }, modifier = Modifier.weight(1f).height(52.dp), shape = RoundedCornerShape(16.dp)) { Icon(Icons.Rounded.AutoAwesome, null); Spacer(Modifier.width(7.dp)); Text("STYLE INTO OUTFIT") }
                IconButton(onClick = { component.deleteGarment(item.id) }, modifier = Modifier.size(52.dp).background(Color(0xFFFFECEC), RoundedCornerShape(16.dp))) { Icon(Icons.Rounded.Delete, "Delete garment", tint = Color(0xFFDC2626)) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AddGarmentSheet(component: HomeComponent) {
    var name by remember { mutableStateOf("") }
    var brand by remember { mutableStateOf("") }
    var slot by remember { mutableStateOf(WardrobeSlot.Top) }
    var imageData by remember { mutableStateOf<ByteArray?>(null) }
    var imageMimeType by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val shape = when (slot) { WardrobeSlot.Head -> GarmentShape.Cap; WardrobeSlot.Top -> GarmentShape.Shirt; WardrobeSlot.Layer -> GarmentShape.Jacket; WardrobeSlot.Bottom -> GarmentShape.Trousers; WardrobeSlot.Shoes -> GarmentShape.Sneakers }
    ModalBottomSheet(onDismissRequest = { component.showAddGarment(false) }, containerColor = LocalAlmariColors.current.white) {
        Column(Modifier.fillMaxWidth().padding(horizontal = 22.dp).padding(bottom = 28.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("ADD GARMENT", style = LocalAlmariTypography.current.display)
            Text("Photograph a piece or choose it from your gallery, then add the details.", color = LocalAlmariColors.current.muted, style = LocalAlmariTypography.current.body)
            if (imageData == null) Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(onClick = { scope.launch { FileKit.openCameraPicker()?.let { file -> imageData = file.readBytes(); imageMimeType = mimeTypeFor(file.extension) } } }, modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(15.dp)) { Icon(Icons.Rounded.PhotoCamera, null); Spacer(Modifier.width(6.dp)); Text("CAMERA") }
                OutlinedButton(onClick = { scope.launch { FileKit.openFilePicker(type = FileKitType.Image)?.let { file -> imageData = file.readBytes(); imageMimeType = mimeTypeFor(file.extension) } } }, modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(15.dp)) { Icon(Icons.Rounded.PhotoLibrary, null); Spacer(Modifier.width(6.dp)); Text("GALLERY") }
            } else Box(Modifier.fillMaxWidth().height(150.dp).background(LocalAlmariColors.current.cloud, RoundedCornerShape(16.dp))) {
                AsyncImage(model = imageData, contentDescription = "Selected garment", contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                IconButton(onClick = { imageData = null; imageMimeType = null }, modifier = Modifier.align(Alignment.TopEnd).padding(6.dp).background(LocalAlmariColors.current.white, CircleShape)) { Icon(Icons.Rounded.Close, "Remove image") }
            }
            OutlinedTextField(name, { name = it }, Modifier.fillMaxWidth(), label = { Text("Garment name") }, singleLine = true, shape = RoundedCornerShape(14.dp))
            OutlinedTextField(brand, { brand = it }, Modifier.fillMaxWidth(), label = { Text("Brand (optional)") }, singleLine = true, shape = RoundedCornerShape(14.dp))
            Text("CATEGORY", style = LocalAlmariTypography.current.label)
            Row(Modifier.fillMaxWidth().horizontalScroll(androidx.compose.foundation.rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) { WardrobeSlot.entries.forEach { FilterChip(slot == it, { slot = it }, { Text(it.label) }) } }
            Button(onClick = { component.addGarment(name, brand, slot, shape, 0xFF3157F6, imageData, imageMimeType) }, enabled = name.isNotBlank(), modifier = Modifier.fillMaxWidth().height(54.dp), shape = RoundedCornerShape(17.dp)) { Text("ADD TO WARDROBE") }
        }
    }
}

@Composable
internal fun SavedScreen(state: HomeState, component: HomeComponent, modifier: Modifier = Modifier) {
    val outfits = state.savedOutfits.filter { when (state.savedFilter) { SavedFilter.All -> true; SavedFilter.Worn -> it.lastWorn != "Not worn yet"; SavedFilter.Favorites -> it.isFavorite } }
    Column(modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Text("SAVED", style = LocalAlmariTypography.current.display, modifier = Modifier.padding(top = 14.dp, bottom = 10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 12.dp)) { SavedFilter.entries.forEach { FilterChip(state.savedFilter == it, { component.setSavedFilter(it) }, { Text(it.label) }) } }
        if (outfits.isEmpty()) Box(Modifier.fillMaxSize(), Alignment.Center) { Text("No looks here yet. Save one from Outfit Studio.", color = LocalAlmariColors.current.muted) }
        else LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp), contentPadding = PaddingValues(bottom = 18.dp)) { items(outfits, key = { it.id }) { OutfitCard(it, component) } }
    }
}

@Composable
private fun OutfitCard(outfit: SavedOutfit, component: HomeComponent) {
    Column(Modifier.fillMaxWidth().shadow(3.dp, RoundedCornerShape(22.dp)).background(LocalAlmariColors.current.white, RoundedCornerShape(22.dp)).border(1.dp, LocalAlmariColors.current.border, RoundedCornerShape(22.dp)).padding(15.dp)) {
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween) {
            Column { Text(outfit.name, fontWeight = FontWeight.Bold, style = LocalAlmariTypography.current.body); Text("Worn ${outfit.lastWorn}", color = LocalAlmariColors.current.muted, style = LocalAlmariTypography.current.caption) }
            IconButton(onClick = { component.toggleFavorite(outfit.id) }) { Icon(if (outfit.isFavorite) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder, "Favorite", tint = LocalAlmariColors.current.cobalt) }
        }
        Row(Modifier.fillMaxWidth().height(150.dp).background(LocalAlmariColors.current.cloud, RoundedCornerShape(16.dp)).padding(8.dp), horizontalArrangement = Arrangement.SpaceEvenly) { outfit.items.take(5).forEach { GarmentVisual(it, Modifier.weight(1f).fillMaxHeight().padding(4.dp)) } }
        Row(Modifier.fillMaxWidth().padding(top = 12.dp), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            Button({ component.wearOutfit(outfit.id) }, Modifier.weight(1f), shape = CircleShape) { Icon(Icons.Rounded.Check, null); Spacer(Modifier.width(5.dp)); Text("WEAR TODAY") }
            OutlinedButton({ component.showOutfit(outfit.id) }, Modifier.weight(1f), shape = CircleShape) { Icon(Icons.Rounded.Edit, null); Spacer(Modifier.width(5.dp)); Text("DETAILS") }
        }
    }
    if (outfit.id == component.state.value.selectedOutfitId) AlertDialog(onDismissRequest = { component.showOutfit(null) }, title = { Text(outfit.name) }, text = { Text("${outfit.items.size} pieces · ${outfit.occasion}\nLast worn: ${outfit.lastWorn}") }, confirmButton = { TextButton({ component.editOutfit(outfit.id) }) { Text("EDIT IN STUDIO") } }, dismissButton = { TextButton({ component.deleteOutfit(outfit.id) }) { Text("DELETE", color = Color(0xFFDC2626)) } })
}

@Composable
private fun GarmentVisual(item: WardrobeItem, modifier: Modifier = Modifier) {
    val image = item.imageData ?: item.imageUrl
    if (image != null) AsyncImage(model = image, contentDescription = item.name, contentScale = ContentScale.Fit, modifier = modifier.padding(8.dp))
    else GarmentIllustration(item.shape, Color(item.colorValue), modifier.padding(18.dp))
}

private fun mimeTypeFor(extension: String): String = when (extension.lowercase()) {
    "png" -> "image/png"
    "webp" -> "image/webp"
    else -> "image/jpeg"
}

@Composable
internal fun ProfileScreen(state: HomeState, component: HomeComponent, modifier: Modifier = Modifier) {
    LazyColumn(modifier.fillMaxSize(), contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
        item {
            Row(Modifier.fillMaxWidth().background(LocalAlmariColors.current.white, RoundedCornerShape(22.dp)).border(1.dp, LocalAlmariColors.current.border, RoundedCornerShape(22.dp)).clickable { component.showProfileEditor(true) }.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(62.dp).background(LocalAlmariColors.current.cobalt, CircleShape), Alignment.Center) { Text(state.profile.name.take(2).uppercase(), color = Color.White, fontWeight = FontWeight.Black) }
                Column(Modifier.padding(start = 14.dp).weight(1f)) { Text(state.profile.name, style = LocalAlmariTypography.current.display); Text(state.profile.handle, color = LocalAlmariColors.current.muted, style = LocalAlmariTypography.current.body); Text("ALMARI FOUNDING MEMBER", color = LocalAlmariColors.current.cobalt, style = LocalAlmariTypography.current.caption) }
                Icon(Icons.Rounded.Edit, "Edit profile", tint = LocalAlmariColors.current.muted)
            }
        }
        item { SectionLabel("ACCOUNT"); SettingsCard { SettingsRow(Icons.Rounded.Badge, "Display name", state.profile.name) { component.showProfileEditor(true) }; SettingsRow(Icons.Rounded.Mail, "Email", state.profile.email); SettingsRow(Icons.Rounded.CloudOff, "Wardrobe storage", "On this device"); if (state.isConnected) SettingsRow(Icons.AutoMirrored.Rounded.Logout, "Sign out", "") { component.signOut() } } }
        item { SectionLabel("PREFERENCES"); SettingsCard { SettingsRow(Icons.Rounded.Palette, "Default aesthetic", state.profile.styleProfile) { component.showProfileEditor(true) }; SettingsRow(if (state.profile.isDarkMode) Icons.Rounded.DarkMode else Icons.Rounded.LightMode, "Appearance", if (state.profile.isDarkMode) "Dark" else "Light", component::toggleAppearance) } }
        item { SectionLabel("WARDROBE & STORAGE"); SettingsCard { SettingsRow(Icons.Rounded.Checkroom, "Indexed wardrobe", "${state.garments.size} items") { component.selectTab(HomeTab.Closet) }; SettingsRow(Icons.Rounded.Bookmarks, "Saved looks", "${state.savedOutfits.size} looks") { component.selectTab(HomeTab.Saved) } } }
        item { SectionLabel("SUPPORT"); SettingsCard { SettingsRow(Icons.AutoMirrored.Rounded.HelpOutline, "Help & FAQ", "Coming soon"); SettingsRow(Icons.Rounded.Shield, "Privacy & terms", "Coming soon"); SettingsRow(Icons.Rounded.Info, "Version", "0.1.0") } }
    }
    if (state.isEditingProfile) ProfileEditDialog(state, component)
}

@Composable private fun SectionLabel(text: String) = Text(text, color = LocalAlmariColors.current.muted, style = LocalAlmariTypography.current.label, modifier = Modifier.padding(start = 4.dp, bottom = 7.dp))
@Composable private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) = Column(Modifier.fillMaxWidth().background(LocalAlmariColors.current.white, RoundedCornerShape(20.dp)).border(1.dp, LocalAlmariColors.current.border, RoundedCornerShape(20.dp)).padding(horizontal = 15.dp), content = content)
@Composable private fun SettingsRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String, onClick: (() -> Unit)? = null) = Row(Modifier.fillMaxWidth().then(if (onClick == null) Modifier else Modifier.clickable(onClick = onClick)).padding(vertical = 15.dp), verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = LocalAlmariColors.current.muted); Text(label, Modifier.padding(start = 12.dp).weight(1f), style = LocalAlmariTypography.current.body); Text(value, color = LocalAlmariColors.current.ink, maxLines = 1, overflow = TextOverflow.Ellipsis, style = LocalAlmariTypography.current.caption); if (onClick != null) Icon(Icons.Rounded.ChevronRight, null, tint = LocalAlmariColors.current.border) }

@Composable private fun ProfileEditDialog(state: HomeState, component: HomeComponent) {
    var name by remember { mutableStateOf(state.profile.name) }; var style by remember { mutableStateOf(state.profile.styleProfile) }
    AlertDialog(onDismissRequest = { component.showProfileEditor(false) }, title = { Text("Edit profile") }, text = { Column(verticalArrangement = Arrangement.spacedBy(10.dp)) { OutlinedTextField(name, { name = it }, label = { Text("Display name") }); OutlinedTextField(style, { style = it }, label = { Text("Default aesthetic") }) } }, confirmButton = { TextButton({ component.updateProfile(name, style) }, enabled = name.isNotBlank() && style.isNotBlank()) { Text("SAVE CHANGES") } }, dismissButton = { TextButton({ component.showProfileEditor(false) }) { Text("CANCEL") } })
}
