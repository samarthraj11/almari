package com.almari.shared.feature.home

import kotlinx.serialization.Serializable

@Serializable enum class WardrobeSlot(val label: String) { Head("HEAD"), Top("TOP"), Layer("LAYER"), Bottom("BOTTOM"), Shoes("SHOES") }
@Serializable enum class GarmentShape { Cap, Kurta, Shirt, Jacket, Trousers, Skirt, Sneakers, Sandals }

@Serializable
data class WardrobeItem(
    val id: String,
    val name: String,
    val shape: GarmentShape,
    val colorValue: Long,
    val brand: String = "Almari edit",
    val wearCount: Int = 0,
    val lastWorn: String = "Not worn yet",
    val styleTag: String = "#EVERYDAY",
    val imageUrl: String? = null,
    val imageData: ByteArray? = null,
    val imageMimeType: String? = null,
)

@Serializable data class WardrobeRail(val slot: WardrobeSlot, val items: List<WardrobeItem>, val selectedIndex: Int, val isLocked: Boolean = false) {
    val selectedItem: WardrobeItem get() = items[selectedIndex.coerceIn(items.indices)]
    val previousItem: WardrobeItem get() = items[(selectedIndex - 1 + items.size) % items.size]
    val nextItem: WardrobeItem get() = items[(selectedIndex + 1) % items.size]
}

@Serializable data class SavedOutfit(
    val id: String,
    val name: String,
    val items: List<WardrobeItem>,
    val occasion: String = "Everyday",
    val lastWorn: String = "Not worn yet",
    val isFavorite: Boolean = true,
    val previewImageUrl: String? = null,
)
@Serializable data class UserProfile(val name: String = "Samarth", val email: String = "samarth@almari.app", val handle: String = "@samarth.styles", val styleProfile: String = "Indo-street / everyday", val isDarkMode: Boolean = false)
enum class ClosetFilter(val label: String) { All("ALL"), Tops("TOPS"), Layers("LAYERS"), Bottoms("BOTTOMS"), Shoes("SHOES") }
enum class SavedFilter(val label: String) { All("All"), Worn("Worn"), Favorites("Favorites") }
enum class HomeTab(val label: String) { Outfit("OUTFIT"), Closet("CLOSET"), Capture("CAPTURE"), Saved("SAVED"), Profile("PROFILE") }
enum class TryOnStage { Hidden, Generating, Ready, Error }

data class HomeState(
    val credits: Int = 48,
    val rails: List<WardrobeRail> = defaultWardrobeRails(),
    val savedOutfits: List<SavedOutfit> = defaultSavedOutfits(),
    val profile: UserProfile = UserProfile(),
    val selectedTab: HomeTab = HomeTab.Outfit,
    val closetFilter: ClosetFilter = ClosetFilter.All,
    val closetQuery: String = "",
    val savedFilter: SavedFilter = SavedFilter.All,
    val selectedGarmentId: String? = null,
    val selectedOutfitId: String? = null,
    val isAddGarmentOpen: Boolean = false,
    val isEditingProfile: Boolean = false,
    val isConnected: Boolean = false,
    val isSyncing: Boolean = false,
    val authError: String? = null,
    val tryOnStage: TryOnStage = TryOnStage.Hidden,
    val tryOnItems: List<WardrobeItem> = emptyList(),
    val tryOnImageUrl: String? = null,
    val tryOnError: String? = null,
    val message: String? = null,
) {
    val garments: List<WardrobeItem> get() = rails.flatMap { it.items }
    val currentOutfit: List<WardrobeItem> get() = rails.map { it.selectedItem }
}

private fun defaultWardrobeRails() = listOf(
    rail(WardrobeSlot.Head, item("head-beret", "Rose beret", GarmentShape.Cap, 0xFFE8A8AE, "Almari edit", 9, "3d ago", "#SOFT"), item("head-olive", "Olive cap", GarmentShape.Cap, 0xFF768258, "Almost Gods", 4)),
    rail(WardrobeSlot.Top, item("top-cream", "Cream knit top", GarmentShape.Shirt, 0xFFF1E4C8, "Uniqlo", 14, "Yesterday", "#MINIMAL"), item("top-blush", "Blush cardigan", GarmentShape.Shirt, 0xFFE8A8AE, "Mango", 8), item("top-sage", "Sage kurta", GarmentShape.Kurta, 0xFF9EAE8B, "Nicobar", 5)),
    rail(WardrobeSlot.Layer, item("layer-pink", "Pink knit jacket", GarmentShape.Jacket, 0xFFE8A8AE, "Almari edit", 19, "4d ago", "#LAYERED"), item("layer-ink", "Ink biker jacket", GarmentShape.Jacket, 0xFF24262D, "Bluorng", 11), item("layer-denim", "Blue denim jacket", GarmentShape.Jacket, 0xFF456A9E, "Levi's", 22), locked = true),
    rail(WardrobeSlot.Bottom, item("bottom-denim-skirt", "Denim mini skirt", GarmentShape.Skirt, 0xFF456A9E, "H&M", 22, "Yesterday", "#DENIM"), item("bottom-black", "Black wide-leg trousers", GarmentShape.Trousers, 0xFF24262D, "Zara", 12), item("bottom-lilac", "Lilac pleated skirt", GarmentShape.Skirt, 0xFF9A86C8, "Mango", 3)),
    rail(WardrobeSlot.Shoes, item("shoes-white", "White sneakers", GarmentShape.Sneakers, 0xFFEEE9DF, "New Balance", 17, "2d ago", "#DAILY"), item("shoes-pink", "Pink slingback heels", GarmentShape.Sandals, 0xFFE8A8AE, "Charles & Keith", 6), item("shoes-black", "Black ankle boots", GarmentShape.Sneakers, 0xFF24262D, "Zara", 10)),
)
private fun rail(slot: WardrobeSlot, vararg items: WardrobeItem, locked: Boolean = false) = WardrobeRail(slot, items.toList(), 0, locked)
private fun item(id: String, name: String, shape: GarmentShape, color: Long, brand: String, wears: Int = 0, last: String = "Not worn yet", tag: String = "#EVERYDAY") = WardrobeItem(id, name, shape, color, brand, wears, last, tag)
private fun defaultSavedOutfits(): List<SavedOutfit> {
    val rails = defaultWardrobeRails()
    return listOf(
        SavedOutfit("coffee-run", "Coffee Run", rails.map { it.items.first() }, "Casual", "2d ago", true),
        SavedOutfit("studio-day", "Studio Day", rails.mapNotNull { it.items.getOrNull(1) }, "Work", "6d ago", false),
        SavedOutfit("night-out", "Night Out", rails.map { it.items.last() }, "Evening", "2w ago", true),
    )
}
