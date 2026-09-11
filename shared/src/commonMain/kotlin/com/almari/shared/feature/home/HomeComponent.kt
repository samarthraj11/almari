package com.almari.shared.feature.home

import kotlinx.coroutines.flow.StateFlow

interface HomeComponent {
    val state: StateFlow<HomeState>
    fun move(slot: WardrobeSlot, direction: Int)
    fun toggleLock(slot: WardrobeSlot)
    fun shuffle()
    fun selectTab(tab: HomeTab)
    fun setClosetQuery(query: String)
    fun setClosetFilter(filter: ClosetFilter)
    fun setSavedFilter(filter: SavedFilter)
    fun showGarment(id: String?)
    fun showOutfit(id: String?)
    fun editOutfit(id: String)
    fun showAddGarment(show: Boolean)
    fun addGarment(name: String, brand: String, slot: WardrobeSlot, shape: GarmentShape, colorValue: Long, imageData: ByteArray? = null, imageMimeType: String? = null)
    fun deleteGarment(id: String)
    fun styleGarment(id: String)
    fun saveCurrentOutfit(name: String)
    fun deleteOutfit(id: String)
    fun wearOutfit(id: String)
    fun toggleFavorite(id: String)
    fun showProfileEditor(show: Boolean)
    fun updateProfile(name: String, styleProfile: String)
    fun toggleAppearance()
    fun beginGoogleSignIn(onReady: () -> Unit)
    fun signOut()
    fun clearMessage()
}
