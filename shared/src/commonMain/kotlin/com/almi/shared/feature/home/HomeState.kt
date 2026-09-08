package com.almi.shared.feature.home

enum class WardrobeSlot(val label: String) {
    Head("HEAD"),
    Top("TOP"),
    Layer("LAYER"),
    Bottom("BOTTOM"),
    Shoes("SHOES"),
}

enum class GarmentShape {
    Cap,
    Kurta,
    Shirt,
    Jacket,
    Trousers,
    Skirt,
    Sneakers,
    Sandals,
}

data class WardrobeItem(
    val name: String,
    val shape: GarmentShape,
    val colorValue: Long,
)

data class WardrobeRail(
    val slot: WardrobeSlot,
    val items: List<WardrobeItem>,
    val selectedIndex: Int,
    val isLocked: Boolean = false,
) {
    val selectedItem: WardrobeItem get() = items[selectedIndex]
    val previousItem: WardrobeItem get() = items[(selectedIndex - 1 + items.size) % items.size]
    val nextItem: WardrobeItem get() = items[(selectedIndex + 1) % items.size]
}

data class HomeState(
    val credits: Int = 48,
    val rails: List<WardrobeRail> = defaultWardrobeRails(),
    val selectedTab: HomeTab = HomeTab.Outfit,
)

enum class HomeTab(val label: String) {
    Outfit("OUTFIT"),
    Closet("CLOSET"),
    Capture("CAPTURE"),
    Saved("SAVED"),
    Profile("PROFILE"),
}

private fun defaultWardrobeRails() = listOf(
    WardrobeRail(
        slot = WardrobeSlot.Head,
        items = listOf(
            WardrobeItem("Indigo cap", GarmentShape.Cap, 0xFF3157F6),
            WardrobeItem("Olive cap", GarmentShape.Cap, 0xFF768258),
            WardrobeItem("Black cap", GarmentShape.Cap, 0xFF24262D),
        ),
        selectedIndex = 0,
    ),
    WardrobeRail(
        slot = WardrobeSlot.Top,
        items = listOf(
            WardrobeItem("Ivory kurta", GarmentShape.Kurta, 0xFFF1E4C8),
            WardrobeItem("Cobalt shirt", GarmentShape.Shirt, 0xFF3157F6),
            WardrobeItem("Sage kurta", GarmentShape.Kurta, 0xFF9EAE8B),
        ),
        selectedIndex = 0,
    ),
    WardrobeRail(
        slot = WardrobeSlot.Layer,
        items = listOf(
            WardrobeItem("Brown overshirt", GarmentShape.Jacket, 0xFF7A4D34),
            WardrobeItem("Ink jacket", GarmentShape.Jacket, 0xFF24262D),
            WardrobeItem("Blue denim", GarmentShape.Jacket, 0xFF456A9E),
        ),
        selectedIndex = 0,
        isLocked = true,
    ),
    WardrobeRail(
        slot = WardrobeSlot.Bottom,
        items = listOf(
            WardrobeItem("Wide-leg denim", GarmentShape.Trousers, 0xFF456A9E),
            WardrobeItem("Black trousers", GarmentShape.Trousers, 0xFF24262D),
            WardrobeItem("Lilac skirt", GarmentShape.Skirt, 0xFF9A86C8),
        ),
        selectedIndex = 0,
    ),
    WardrobeRail(
        slot = WardrobeSlot.Shoes,
        items = listOf(
            WardrobeItem("Street sneakers", GarmentShape.Sneakers, 0xFFEEE9DF),
            WardrobeItem("Cobalt sneakers", GarmentShape.Sneakers, 0xFF3157F6),
            WardrobeItem("Tan sandals", GarmentShape.Sandals, 0xFFB77B4F),
        ),
        selectedIndex = 0,
    ),
)
