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
)

@Serializable data class WardrobeRail(val slot: WardrobeSlot, val items: List<WardrobeItem>, val selectedIndex: Int, val isLocked: Boolean = false) {
    val selectedItem: WardrobeItem get() = items[selectedIndex.coerceIn(items.indices)]
    val previousItem: WardrobeItem get() = items[(selectedIndex - 1 + items.size) % items.size]
    val nextItem: WardrobeItem get() = items[(selectedIndex + 1) % items.size]
}

@Serializable data class SavedOutfit(val id: String, val name: String, val items: List<WardrobeItem>, val occasion: String = "Everyday", val lastWorn: String = "Not worn yet", val isFavorite: Boolean = true)
@Serializable data class UserProfile(val name: String = "Samarth", val email: String = "samarth@almari.app", val handle: String = "@samarth.styles", val styleProfile: String = "Indo-street / everyday", val isDarkMode: Boolean = false)
enum class ClosetFilter(val label: String) { All("ALL"), Tops("TOPS"), Layers("LAYERS"), Bottoms("BOTTOMS"), Shoes("SHOES") }
enum class SavedFilter(val label: String) { All("All"), Worn("Worn"), Favorites("Favorites") }
enum class HomeTab(val label: String) { Outfit("OUTFIT"), Closet("CLOSET"), Capture("CAPTURE"), Saved("SAVED"), Profile("PROFILE") }

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
    val isAuthOpen: Boolean = false,
    val isConnected: Boolean = false,
    val isSyncing: Boolean = false,
    val authError: String? = null,
    val message: String? = null,
) {
    val garments: List<WardrobeItem> get() = rails.flatMap { it.items }
    val currentOutfit: List<WardrobeItem> get() = rails.map { it.selectedItem }
}

private fun defaultWardrobeRails() = listOf(
    rail(WardrobeSlot.Head, item("head-indigo", "Indigo cap", GarmentShape.Cap, 0xFF3157F6, "Capsul", 9, "3d ago", "#STREET"), item("head-olive", "Olive cap", GarmentShape.Cap, 0xFF768258, "Almost Gods", 4)),
    rail(WardrobeSlot.Top, item("top-kurta", "Ivory kurta", GarmentShape.Kurta, 0xFFF1E4C8, "Fabindia", 14, "Yesterday", "#INDO"), item("top-shirt", "Cobalt shirt", GarmentShape.Shirt, 0xFF3157F6, "Uniqlo", 8), item("top-sage", "Sage kurta", GarmentShape.Kurta, 0xFF9EAE8B, "Nicobar", 5)),
    rail(WardrobeSlot.Layer, item("layer-brown", "Brown overshirt", GarmentShape.Jacket, 0xFF7A4D34, "Jaywalking", 19, "4d ago", "#LAYERED"), item("layer-ink", "Ink jacket", GarmentShape.Jacket, 0xFF24262D, "Bluorng", 11), item("layer-denim", "Blue denim", GarmentShape.Jacket, 0xFF456A9E, "Levi's", 22), locked = true),
    rail(WardrobeSlot.Bottom, item("bottom-denim", "Wide-leg denim", GarmentShape.Trousers, 0xFF456A9E, "Acne Studios", 22, "Yesterday", "#BAGGY"), item("bottom-black", "Black trousers", GarmentShape.Trousers, 0xFF24262D, "Zara", 12), item("bottom-skirt", "Lilac skirt", GarmentShape.Skirt, 0xFF9A86C8, "H&M", 3)),
    rail(WardrobeSlot.Shoes, item("shoes-street", "Street sneakers", GarmentShape.Sneakers, 0xFFEEE9DF, "New Balance", 17, "2d ago", "#DAILY"), item("shoes-blue", "Cobalt sneakers", GarmentShape.Sneakers, 0xFF3157F6, "Adidas", 6), item("shoes-tan", "Tan sandals", GarmentShape.Sandals, 0xFFB77B4F, "Birkenstock", 10)),
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
