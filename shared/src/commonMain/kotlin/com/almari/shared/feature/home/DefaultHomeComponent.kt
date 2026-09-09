package com.almari.shared.feature.home

import com.almari.shared.data.AlmariApi
import com.almari.shared.data.GarmentDto
import com.almari.shared.data.GarmentInput
import com.almari.shared.data.loadHomeState
import com.almari.shared.data.saveHomeState
import com.almari.shared.data.HomeStateStorage
import com.almari.shared.data.GoogleAuthBridge
import com.almari.shared.data.SessionDto
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

class DefaultHomeComponent(private val api: AlmariApi? = null) : HomeComponent {
    private val mutableState = MutableStateFlow(loadHomeState())
    override val state: StateFlow<HomeState> = mutableState.asStateFlow()
    private var sequence = 100
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    init {
        scope.launch { mutableState.drop(1).collect(::saveHomeState) }
        scope.launch {
            GoogleAuthBridge.pending.collect { redirect ->
                redirect ?: return@collect
                GoogleAuthBridge.clear()
                redirect.code?.let(::completeGoogleSignIn)
                    ?: mutableState.update { it.copy(authError = "Google sign-in was cancelled or could not be completed.") }
            }
        }
        HomeStateStorage.readToken()?.let { token ->
            api?.restoreToken(token)
            mutableState.update { it.copy(isConnected = true) }
            scope.launch { syncInternal(seedWhenEmpty = false) }
        }
    }

    override fun move(slot: WardrobeSlot, direction: Int) = updateRail(slot) { rail -> if (rail.isLocked) rail else rail.copy(selectedIndex = (rail.selectedIndex + direction).mod(rail.items.size)) }
    override fun toggleLock(slot: WardrobeSlot) = updateRail(slot) { it.copy(isLocked = !it.isLocked) }
    override fun shuffle() {
        val snapshot = mutableState.value
        if (snapshot.isConnected && snapshot.garments.none { it.id.startsWith("local-") }) {
            scope.launch {
                mutableState.update { it.copy(isSyncing = true) }
                runCatching { api?.generateOutfit(snapshot.rails.filter { it.isLocked }.map { it.selectedItem.id }) }
                    .onSuccess { generated ->
                        if (generated != null) mutableState.update { state -> state.copy(
                            credits = generated.credits,
                            rails = state.rails.map { rail ->
                                val chosen = generated.items.firstOrNull { it.category == rail.slot.name.uppercase() }
                                val index = chosen?.let { selected -> rail.items.indexOfFirst { it.id == selected.id } } ?: -1
                                if (rail.isLocked || index < 0) rail else rail.copy(selectedIndex = index)
                            },
                            isSyncing = false,
                            message = "Fresh look ready",
                        )
                        }
                    }
                    .onFailure { error -> showNetworkError(error); mutableState.update { it.copy(isSyncing = false) } }
            }
            return
        }
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
        if (mutableState.value.isConnected) scope.launch {
            runCatching {
                val imageUrl = imageData?.let { api?.uploadImage(it, imageMimeType ?: "image/jpeg") }
                api?.addGarment(item.toInput(slot).copy(imageUrl = imageUrl))
            }.onSuccess { remote -> if (remote != null) replaceItem(item.id, remote.toItem()) }.onFailure(::showNetworkError)
        }
    }
    override fun deleteGarment(id: String) {
        mutableState.update { state -> state.copy(
            rails = state.rails.map { rail -> val remaining = rail.items.filterNot { it.id == id }; if (remaining.isEmpty()) rail else rail.copy(items = remaining, selectedIndex = rail.selectedIndex.coerceAtMost(remaining.lastIndex)) },
            selectedGarmentId = null, message = "Garment removed",
        ) }
        if (mutableState.value.isConnected && !id.startsWith("local-")) scope.launch { runCatching { api?.deleteGarment(id) }.onFailure(::showNetworkError) }
    }
    override fun styleGarment(id: String) = mutableState.update { state -> state.copy(rails = state.rails.map { rail -> val index = rail.items.indexOfFirst { it.id == id }; if (index >= 0) rail.copy(selectedIndex = index, isLocked = true) else rail }, selectedTab = HomeTab.Outfit, selectedGarmentId = null, message = "Garment locked into your look") }
    override fun saveCurrentOutfit(name: String) {
        val state = mutableState.value
        val localId = "saved-${sequence++}"
        val displayName = name.ifBlank { "New look" }
        mutableState.update { it.copy(savedOutfits = listOf(SavedOutfit(localId, displayName, state.currentOutfit)) + it.savedOutfits, message = "Look saved") }
        if (state.isConnected && state.currentOutfit.none { it.id.startsWith("local-") }) scope.launch {
            runCatching { api?.saveOutfit(displayName, "Everyday", state.currentOutfit.map { it.id }) }
                .onSuccess { remote -> if (remote != null) mutableState.update { current -> current.copy(savedOutfits = current.savedOutfits.map { if (it.id == localId) it.copy(id = remote.id) else it }) } }
                .onFailure(::showNetworkError)
        }
    }
    override fun deleteOutfit(id: String) {
        mutableState.update { it.copy(savedOutfits = it.savedOutfits.filterNot { outfit -> outfit.id == id }, selectedOutfitId = null, message = "Saved look removed") }
        if (mutableState.value.isConnected && !id.startsWith("saved-")) scope.launch { runCatching { api?.deleteOutfit(id) }.onFailure(::showNetworkError) }
    }
    override fun wearOutfit(id: String) {
        mutableState.update { state -> state.copy(savedOutfits = state.savedOutfits.map { if (it.id == id) it.copy(lastWorn = "Today") else it }, message = "Outfit logged for today") }
        if (mutableState.value.isConnected && !id.startsWith("saved-")) scope.launch { runCatching { api?.wearOutfit(id) }.onFailure(::showNetworkError) }
    }
    override fun toggleFavorite(id: String) {
        val favorite = mutableState.value.savedOutfits.firstOrNull { it.id == id }?.isFavorite?.not() ?: return
        mutableState.update { state -> state.copy(savedOutfits = state.savedOutfits.map { if (it.id == id) it.copy(isFavorite = favorite) else it }) }
        if (mutableState.value.isConnected && !id.startsWith("saved-")) scope.launch { runCatching { api?.setOutfitFavorite(id, favorite) }.onFailure(::showNetworkError) }
    }
    override fun showProfileEditor(show: Boolean) = mutableState.update { it.copy(isEditingProfile = show) }
    override fun updateProfile(name: String, styleProfile: String) {
        mutableState.update { it.copy(profile = it.profile.copy(name = name.trim(), styleProfile = styleProfile.trim()), isEditingProfile = false, message = "Profile updated") }
        if (mutableState.value.isConnected) scope.launch { runCatching { api?.updateProfile(name.trim(), styleProfile.trim()) }.onFailure(::showNetworkError) }
    }
    override fun toggleAppearance() = mutableState.update { it.copy(profile = it.profile.copy(isDarkMode = !it.profile.isDarkMode), message = "Appearance preference saved") }
    override fun showAuth(show: Boolean) = mutableState.update { it.copy(isAuthOpen = show, authError = null) }
    override fun authenticate(name: String, email: String, password: String, register: Boolean) {
        if (api == null) return
        mutableState.update { it.copy(isSyncing = true, authError = null) }
        scope.launch {
            runCatching { if (register) api.register(name, email, password) else api.login(email, password) }
                .onSuccess { session ->
                    connectSession(session)
                }
                .onFailure { error -> mutableState.update { it.copy(isSyncing = false, authError = error.message ?: "Could not connect to Almari backend") } }
        }
    }
    override fun completeGoogleSignIn(code: String) {
        val service = api ?: return
        mutableState.update { it.copy(isSyncing = true, authError = null) }
        scope.launch {
            runCatching { service.exchangeGoogleCode(code) }
                .onSuccess { connectSession(it) }
                .onFailure { error -> mutableState.update { it.copy(isSyncing = false, authError = error.message ?: "Google sign-in failed. Try again.") } }
        }
    }
    override fun signOut() { api?.signOut(); HomeStateStorage.writeToken(null); mutableState.update { it.copy(isConnected = false, message = "Signed out of sync") } }
    override fun sync() { if (mutableState.value.isConnected) scope.launch { syncInternal(seedWhenEmpty = false) } else showAuth(true) }
    override fun clearMessage() = mutableState.update { it.copy(message = null) }
    private fun updateRail(slot: WardrobeSlot, transform: (WardrobeRail) -> WardrobeRail) = mutableState.update { state -> state.copy(rails = state.rails.map { if (it.slot == slot) transform(it) else it }) }

    private suspend fun syncInternal(seedWhenEmpty: Boolean) {
        val service = api ?: return
        mutableState.update { it.copy(isSyncing = true) }
        runCatching {
            var remote = service.garments()
            if (remote.isEmpty() && seedWhenEmpty) {
                mutableState.value.rails.forEach { rail -> rail.items.forEach { item ->
                    val imageUrl = item.imageData?.let { service.uploadImage(it, "image/jpeg") } ?: item.imageUrl
                    service.addGarment(item.toInput(rail.slot).copy(imageUrl = imageUrl))
                } }
                remote = service.garments()
            }
            Triple(remote, service.outfits(), service.profile())
        }.onSuccess { (remote, remoteOutfits, user) ->
            if (remote.isNotEmpty()) mutableState.update { state -> state.copy(
                rails = state.rails.map { rail -> val items = remote.filter { it.category == rail.slot.name.uppercase() }.map { it.toItem() }; if (items.isEmpty()) rail else rail.copy(items = items, selectedIndex = 0) },
                savedOutfits = remoteOutfits.takeIf { it.isNotEmpty() }?.map { outfit -> SavedOutfit(outfit.id, outfit.name, outfit.items.map { it.toItem() }, outfit.occasion, outfit.lastWorn ?: "Not worn yet", outfit.isFavorite) } ?: state.savedOutfits,
                profile = state.profile.copy(name = user.name, email = user.email, styleProfile = user.styleProfile),
                credits = user.credits,
                isSyncing = false,
                message = "Wardrobe synced",
            ) }
            else mutableState.update { it.copy(isSyncing = false) }
        }.onFailure { error -> showNetworkError(error); mutableState.update { it.copy(isSyncing = false) } }
    }

    private fun replaceItem(localId: String, item: WardrobeItem) = mutableState.update { state -> state.copy(rails = state.rails.map { rail -> rail.copy(items = rail.items.map { if (it.id == localId) item else it }) }) }
    private fun showNetworkError(error: Throwable) = mutableState.update { it.copy(message = error.message ?: "Sync failed. Your local changes are safe.") }
    private fun connectSession(session: SessionDto) {
        HomeStateStorage.writeToken(session.token)
        mutableState.update { state -> state.copy(profile = state.profile.copy(name = session.user.name, email = session.user.email, styleProfile = session.user.styleProfile), credits = session.user.credits, isConnected = true, isAuthOpen = false, isSyncing = false, authError = null, message = "Wardrobe connected") }
        scope.launch { syncInternal(seedWhenEmpty = true) }
    }
}

private fun WardrobeItem.toInput(slot: WardrobeSlot) = GarmentInput(name, slot.name.uppercase(), shape.name.uppercase(), "#" + colorValue.toString(16).takeLast(6).uppercase(), null, brand)
private fun GarmentDto.toItem() = WardrobeItem(id, name, runCatching { GarmentShape.valueOf(shape.lowercase().replaceFirstChar { it.uppercase() }) }.getOrDefault(GarmentShape.Shirt), colorHex.removePrefix("#").toLong(16) or 0xFF000000, brand ?: "Unbranded", imageUrl = imageUrl)
