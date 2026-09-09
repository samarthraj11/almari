package com.almi.shared.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

expect fun platformHttpClient(): HttpClient
expect val defaultApiBaseUrl: String

fun createAlmiApi(baseUrl: String = defaultApiBaseUrl): AlmiApi {
    val client = platformHttpClient().config {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true; explicitNulls = false }) }
    }
    return AlmiApi(client, baseUrl.trimEnd('/'))
}

class AlmiApi(private val client: HttpClient, private val baseUrl: String) {
    private var token: String? = null

    suspend fun register(name: String, email: String, password: String): SessionDto = client.post("$baseUrl/auth/register") { contentType(ContentType.Application.Json); setBody(AuthRequest(email, password, name)) }.body<SessionDto>().also { token = it.token }
    suspend fun login(email: String, password: String): SessionDto = client.post("$baseUrl/auth/login") { contentType(ContentType.Application.Json); setBody(AuthRequest(email, password)) }.body<SessionDto>().also { token = it.token }
    fun restoreToken(value: String?) { token = value }
    fun signOut() { token = null }
    suspend fun garments(): List<GarmentDto> = client.get("$baseUrl/garments") { authorize() }.body()
    suspend fun addGarment(input: GarmentInput): GarmentDto = client.post("$baseUrl/garments") { authorize(); contentType(ContentType.Application.Json); setBody(input) }.body()
    suspend fun deleteGarment(id: String) { client.delete("$baseUrl/garments/$id") { authorize() } }
    suspend fun uploadImage(bytes: ByteArray, mimeType: String): String = client.post("$baseUrl/uploads") {
        authorize()
        setBody(MultiPartFormDataContent(formData {
            append("file", bytes, Headers.build {
                append(HttpHeaders.ContentType, mimeType)
                append(HttpHeaders.ContentDisposition, "filename=garment.${if (mimeType == "image/png") "png" else if (mimeType == "image/webp") "webp" else "jpg"}")
            })
        }))
    }.body<UploadDto>().url
    suspend fun generateOutfit(lockedIds: List<String>): GeneratedOutfitDto = client.post("$baseUrl/outfits/generate") { authorize(); contentType(ContentType.Application.Json); setBody(LockedRequest(lockedIds)) }.body()
    suspend fun outfits(): List<OutfitDto> = client.get("$baseUrl/outfits") { authorize() }.body()
    suspend fun saveOutfit(name: String, occasion: String, ids: List<String>): OutfitDto = client.post("$baseUrl/outfits") { authorize(); contentType(ContentType.Application.Json); setBody(SaveOutfitRequest(name, occasion, ids)) }.body()
    suspend fun deleteOutfit(id: String) { client.delete("$baseUrl/outfits/$id") { authorize() } }
    suspend fun setOutfitFavorite(id: String, favorite: Boolean): OutfitDto = client.patch("$baseUrl/outfits/$id") { authorize(); contentType(ContentType.Application.Json); setBody(OutfitUpdateRequest(favorite)) }.body()
    suspend fun wearOutfit(id: String): OutfitDto = client.post("$baseUrl/outfits/$id/wear") { authorize() }.body()
    suspend fun profile(): UserDto = client.get("$baseUrl/profile") { authorize() }.body()
    suspend fun updateProfile(name: String, style: String): UserDto = client.patch("$baseUrl/profile") { authorize(); contentType(ContentType.Application.Json); setBody(ProfileRequest(name, style)) }.body()
    private fun HttpRequestBuilder.authorize() { token?.let { bearerAuth(it) } }
}

@Serializable data class AuthRequest(val email: String, val password: String, val name: String? = null)
@Serializable data class SessionDto(val token: String, val user: UserDto)
@Serializable data class UserDto(val id: String, val email: String, val name: String, @SerialName("style_profile") val styleProfile: String, val credits: Int)
@Serializable data class GarmentDto(val id: String, val name: String, val category: String, val shape: String, @SerialName("color_hex") val colorHex: String, @SerialName("image_url") val imageUrl: String? = null, val brand: String? = null, val season: String = "ALL")
@Serializable data class GarmentInput(val name: String, val category: String, val shape: String, val colorHex: String, val imageUrl: String? = null, val brand: String? = null, val season: String = "ALL")
@Serializable data class LockedRequest(val lockedIds: List<String>)
@Serializable data class GeneratedOutfitDto(val items: List<GarmentDto>, val credits: Int)
@Serializable data class OutfitDto(val id: String, val name: String, val occasion: String, val items: List<GarmentDto> = emptyList(), @SerialName("is_favorite") val isFavorite: Boolean = true, @SerialName("last_worn") val lastWorn: String? = null)
@Serializable data class SaveOutfitRequest(val name: String, val occasion: String, val garmentIds: List<String>)
@Serializable data class OutfitUpdateRequest(val isFavorite: Boolean)
@Serializable data class ProfileRequest(val name: String, val styleProfile: String)
@Serializable data class UploadDto(val url: String)
