package com.almi.shared.data

import com.almi.shared.feature.home.HomeState
import com.almi.shared.feature.home.SavedOutfit
import com.almi.shared.feature.home.UserProfile
import com.almi.shared.feature.home.WardrobeRail
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

expect object HomeStateStorage {
    fun read(): String?
    fun write(value: String)
    fun readToken(): String?
    fun writeToken(value: String?)
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
