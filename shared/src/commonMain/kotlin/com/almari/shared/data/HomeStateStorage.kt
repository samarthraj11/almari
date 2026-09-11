package com.almari.shared.data

import com.almari.shared.feature.home.HomeState
import com.almari.shared.feature.home.SavedOutfit
import com.almari.shared.feature.home.UserProfile
import com.almari.shared.feature.home.WardrobeRail
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

expect object HomeStateStorage {
    fun read(): String?
    fun write(value: String)
}

@Serializable
private data class PersistedHome(val credits: Int, val rails: List<WardrobeRail>, val savedOutfits: List<SavedOutfit>, val profile: UserProfile)

private val storageJson = Json { ignoreUnknownKeys = true }

fun loadHomeState(): HomeState = runCatching {
    val saved = storageJson.decodeFromString<PersistedHome>(HomeStateStorage.read() ?: return HomeState())
    HomeState(credits = saved.credits, rails = saved.rails, savedOutfits = saved.savedOutfits, profile = saved.profile)
}.getOrElse { HomeState() }

fun saveHomeState(state: HomeState) {
    runCatching { HomeStateStorage.write(storageJson.encodeToString(PersistedHome(state.credits, state.rails, state.savedOutfits, state.profile))) }
}
