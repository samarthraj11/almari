package com.almi.shared.feature.home

import com.almi.shared.data.GoogleAuthBridge
import com.almi.shared.data.handleGoogleAuthRedirect
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class DefaultHomeComponentTest {
    @Test
    fun lockedRailStaysSelectedWhenShuffling() {
        val component = DefaultHomeComponent()
        val before = component.state.value.rails.first { it.slot == WardrobeSlot.Layer }.selectedIndex

        component.shuffle()

        val after = component.state.value.rails.first { it.slot == WardrobeSlot.Layer }.selectedIndex
        assertEquals(before, after)
    }

    @Test
    fun unlockedRailMovesAndConsumesCreditWhenShuffling() {
        val component = DefaultHomeComponent()
        val before = component.state.value

        component.shuffle()

        val after = component.state.value
        assertEquals(before.credits - 1, after.credits)
        assertNotEquals(before.rails.first().selectedIndex, after.rails.first().selectedIndex)
    }

    @Test
    fun garmentCanBeAddedAndStyledFromCloset() {
        val component = DefaultHomeComponent()
        val before = component.state.value.garments.size

        component.addGarment("Test tee", "Local", WardrobeSlot.Top, GarmentShape.Shirt, 0xFF2563EB)
        val added = component.state.value.garments.first { it.name == "Test tee" }
        component.styleGarment(added.id)

        assertEquals(before + 1, component.state.value.garments.size)
        assertEquals(HomeTab.Outfit, component.state.value.selectedTab)
        assertTrue(component.state.value.rails.first { it.slot == WardrobeSlot.Top }.isLocked)
    }

    @Test
    fun savedLookCanBeLoadedAndAppearancePersistsInState() {
        val component = DefaultHomeComponent()
        val outfit = component.state.value.savedOutfits.last()
        val wasDark = component.state.value.profile.isDarkMode

        component.editOutfit(outfit.id)
        component.toggleAppearance()

        assertEquals(HomeTab.Outfit, component.state.value.selectedTab)
        assertEquals(!wasDark, component.state.value.profile.isDarkMode)
    }

    @Test
    fun googleDeepLinkCapturesOneTimeExchangeCode() {
        GoogleAuthBridge.clear()
        handleGoogleAuthRedirect("almi://auth?code=123e4567-e89b-12d3-a456-426614174000")

        assertEquals("123e4567-e89b-12d3-a456-426614174000", GoogleAuthBridge.pending.value?.code)
        GoogleAuthBridge.clear()
    }
}
