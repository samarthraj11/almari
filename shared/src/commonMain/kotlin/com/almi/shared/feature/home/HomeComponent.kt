package com.almi.shared.feature.home

import kotlinx.coroutines.flow.StateFlow

interface HomeComponent {
    val state: StateFlow<HomeState>

    fun move(slot: WardrobeSlot, direction: Int)
    fun toggleLock(slot: WardrobeSlot)
    fun shuffle()
    fun selectTab(tab: HomeTab)
}
