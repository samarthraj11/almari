package com.almi.shared.feature.home

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

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
}
