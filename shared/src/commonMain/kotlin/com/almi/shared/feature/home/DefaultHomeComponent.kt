package com.almi.shared.feature.home

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class DefaultHomeComponent : HomeComponent {
    private val mutableState = MutableStateFlow(HomeState())
    override val state: StateFlow<HomeState> = mutableState.asStateFlow()
    private var shuffleStep = 0

    override fun move(slot: WardrobeSlot, direction: Int) {
        mutableState.update { current ->
            current.copy(
                rails = current.rails.map { rail ->
                    if (rail.slot != slot || rail.isLocked) {
                        rail
                    } else {
                        rail.copy(selectedIndex = nextIndex(rail.selectedIndex, direction, rail.items.size))
                    }
                },
            )
        }
    }

    override fun toggleLock(slot: WardrobeSlot) {
        mutableState.update { current ->
            current.copy(
                rails = current.rails.map { rail ->
                    if (rail.slot == slot) rail.copy(isLocked = !rail.isLocked) else rail
                },
            )
        }
    }

    override fun shuffle() {
        shuffleStep += 1
        mutableState.update { current ->
            current.copy(
                credits = (current.credits - 1).coerceAtLeast(0),
                rails = current.rails.mapIndexed { index, rail ->
                    if (rail.isLocked) rail else rail.copy(
                        selectedIndex = (rail.selectedIndex + shuffleStep + index + 1) % rail.items.size,
                    )
                },
            )
        }
    }

    override fun selectTab(tab: HomeTab) {
        mutableState.update { it.copy(selectedTab = tab) }
    }

    private fun nextIndex(current: Int, direction: Int, itemCount: Int): Int {
        return (current + direction).mod(itemCount)
    }
}
