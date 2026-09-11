package com.almari.shared.feature.home

import com.almari.shared.data.loadHomeState
import com.almari.shared.data.saveHomeState
import com.almari.shared.data.FirebaseAuthBridge
import com.almari.shared.data.FirebaseUserInfo
import com.almari.shared.data.requestFirebaseSignOut
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class DefaultHomeComponent : HomeComponent {
    private val mutableState = MutableStateFlow(loadHomeState())
    override val state: StateFlow<HomeState> = mutableState.asStateFlow()
    private var sequence = 100
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        scope.launch { mutableState.drop(1).collect(::saveHomeState) }
        scope.launch {
            FirebaseAuthBridge.pending.collect { result ->
                result ?: return@collect
                FirebaseAuthBridge.clear()
                when {
                    result.user != null -> connectFirebaseUser(result.user)
                    else -> mutableState.update { it.copy(isSyncing = false, authError = result.error ?: "Google sign-in was cancelled or could not be completed.") }
                }
            }
        }
    }

    override fun move(slot: WardrobeSlot, direction: Int) = updateRail(slot) { rail -> if (rail.isLocked) rail else rail.copy(selectedIndex = (rail.selectedIndex + direction).mod(rail.items.size)) }
    override fun toggleLock(slot: WardrobeSlot) = updateRail(slot) { it.copy(isLocked = !it.isLocked) }
    override fun shuffle() {
        mutableState.update { state ->
            if (state.credits == 0) state.copy(message = "No styling credits left") else state.copy(
                credits = state.credits - 1,
                rails = state.rails.mapIndexed { index, rail -> if (rail.isLocked) rail else rail.copy(selectedIndex = (rail.selectedIndex + index + sequence + 1).mod(rail.items.size)) },
                message = "Fresh look ready",
            )
        }
        sequence++
    }
    override fun selectTab(tab: HomeTab) = mutableState.update { it.copy(selectedTab = tab, isAddGarmentOpen = tab == HomeTab.Capture || it.isAddGarmentOpen) }
    override fun setClosetQuery(query: String) = mutableState.update { it.copy(closetQuery = query) }
    override fun setClosetFilter(filter: ClosetFilter) = mutableState.update { it.copy(closetFilter = filter) }
    override fun setSavedFilter(filter: SavedFilter) = mutableState.update { it.copy(savedFilter = filter) }
    override fun showGarment(id: String?) = mutableState.update { it.copy(selectedGarmentId = id) }
    override fun showOutfit(id: String?) = mutableState.update { it.copy(selectedOutfitId = id) }
    override fun editOutfit(id: String) = mutableState.update { state ->
        val outfit = state.savedOutfits.firstOrNull { it.id == id } ?: return@update state
        state.copy(
            rails = state.rails.map { rail ->
                val selected = outfit.items.firstOrNull { item -> item.id in rail.items.map(WardrobeItem::id) }
                if (selected == null) rail else rail.copy(selectedIndex = rail.items.indexOfFirst { it.id == selected.id })
            },
            selectedOutfitId = null,
            selectedTab = HomeTab.Outfit,
            message = "Look loaded into Outfit Studio",
        )
    }
    override fun showAddGarment(show: Boolean) = mutableState.update { it.copy(isAddGarmentOpen = show, selectedTab = if (!show && it.selectedTab == HomeTab.Capture) HomeTab.Closet else it.selectedTab) }
    override fun addGarment(name: String, brand: String, slot: WardrobeSlot, shape: GarmentShape, colorValue: Long, imageData: ByteArray?, imageMimeType: String?) {
        val item = WardrobeItem("local-${sequence++}", name.trim(), shape, colorValue, brand.trim().ifBlank { "Unbranded" }, imageData = imageData)
        mutableState.update { state -> state.copy(rails = state.rails.map { if (it.slot == slot) it.copy(items = it.items + item) else it }, selectedTab = HomeTab.Closet, isAddGarmentOpen = false, message = "$name added to your closet") }
    }
    override fun deleteGarment(id: String) {
        mutableState.update { state -> state.copy(
            rails = state.rails.map { rail -> val remaining = rail.items.filterNot { it.id == id }; if (remaining.isEmpty()) rail else rail.copy(items = remaining, selectedIndex = rail.selectedIndex.coerceAtMost(remaining.lastIndex)) },
            selectedGarmentId = null, message = "Garment removed",
        ) }
    }
    override fun styleGarment(id: String) = mutableState.update { state -> state.copy(rails = state.rails.map { rail -> val index = rail.items.indexOfFirst { it.id == id }; if (index >= 0) rail.copy(selectedIndex = index, isLocked = true) else rail }, selectedTab = HomeTab.Outfit, selectedGarmentId = null, message = "Garment locked into your look") }
    override fun saveCurrentOutfit(name: String) {
        val state = mutableState.value
        val localId = "saved-${sequence++}"
        val displayName = name.ifBlank { "New look" }
        mutableState.update { it.copy(savedOutfits = listOf(SavedOutfit(localId, displayName, state.currentOutfit)) + it.savedOutfits, message = "Look saved") }
    }
    override fun deleteOutfit(id: String) {
        mutableState.update { it.copy(savedOutfits = it.savedOutfits.filterNot { outfit -> outfit.id == id }, selectedOutfitId = null, message = "Saved look removed") }
    }
    override fun wearOutfit(id: String) {
        mutableState.update { state -> state.copy(savedOutfits = state.savedOutfits.map { if (it.id == id) it.copy(lastWorn = "Today") else it }, message = "Outfit logged for today") }
    }
    override fun toggleFavorite(id: String) {
        val favorite = mutableState.value.savedOutfits.firstOrNull { it.id == id }?.isFavorite?.not() ?: return
        mutableState.update { state -> state.copy(savedOutfits = state.savedOutfits.map { if (it.id == id) it.copy(isFavorite = favorite) else it }) }
    }
    override fun showProfileEditor(show: Boolean) = mutableState.update { it.copy(isEditingProfile = show) }
    override fun updateProfile(name: String, styleProfile: String) {
        mutableState.update { it.copy(profile = it.profile.copy(name = name.trim(), styleProfile = styleProfile.trim()), isEditingProfile = false, message = "Profile updated") }
    }
    override fun toggleAppearance() = mutableState.update { it.copy(profile = it.profile.copy(isDarkMode = !it.profile.isDarkMode), message = "Appearance preference saved") }
    override fun beginGoogleSignIn(onReady: () -> Unit) {
        mutableState.update { it.copy(isSyncing = true, authError = null) }
        onReady()
    }
    override fun signOut() {
        requestFirebaseSignOut()
        mutableState.update { it.copy(isConnected = false, isSyncing = false, message = "Signed out") }
    }
    override fun clearMessage() = mutableState.update { it.copy(message = null) }
    private fun updateRail(slot: WardrobeSlot, transform: (WardrobeRail) -> WardrobeRail) = mutableState.update { state -> state.copy(rails = state.rails.map { if (it.slot == slot) transform(it) else it }) }

    private fun connectFirebaseUser(user: FirebaseUserInfo) {
        val email = user.email.ifBlank { "Signed in with Google" }
        val name = user.displayName.ifBlank { email.substringBefore('@').ifBlank { "Almari member" } }
        mutableState.update { state -> state.copy(
            profile = state.profile.copy(
                name = name,
                email = email,
                handle = "@${email.substringBefore('@').replace(Regex("[^A-Za-z0-9_.]"), "").ifBlank { "almari" }}",
            ),
            isConnected = true,
            isSyncing = false,
            authError = null,
            message = "Signed in with Google",
        ) }
    }
}
