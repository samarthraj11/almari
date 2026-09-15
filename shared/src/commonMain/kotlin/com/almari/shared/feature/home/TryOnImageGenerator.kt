package com.almari.shared.feature.home

import com.almari.shared.data.requestFirebaseIdToken
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

interface TryOnImageGenerator {
    suspend fun generate(items: List<WardrobeItem>): String
}

internal expect fun platformTryOnHttpClient(): HttpClient
internal expect fun platformTryOnBaseUrl(): String

class ApiTryOnImageGenerator(
    private val client: HttpClient = platformTryOnHttpClient().config {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
    },
    private val baseUrl: String = platformTryOnBaseUrl(),
) : TryOnImageGenerator {
    @OptIn(ExperimentalEncodingApi::class)
    override suspend fun generate(items: List<WardrobeItem>): String {
        val token = requestFirebaseIdToken()
            ?: error("Your Google session expired. Sign in again to create an AI try-on.")
        val response = client.post("${baseUrl.trimEnd('/')}/try-on") {
            bearerAuth(token)
            contentType(ContentType.Application.Json)
            setBody(
                TryOnRequest(
                    garments = items.map { item ->
                        TryOnGarment(
                            name = item.name,
                            category = items.slotOf(item).label,
                            colorHex = item.colorValue.toColorHex(),
                            imageBase64 = item.imageData?.let(Base64.Default::encode),
                            mimeType = item.imageMimeType ?: "image/jpeg",
                        )
                    },
                ),
            )
        }
        if (!response.status.isSuccess()) {
            val message = runCatching { response.body<TryOnError>().message }.getOrNull()
            error(message ?: "AI try-on could not be created (${response.status.value}).")
        }
        val imageUrl = response.body<TryOnResponse>().imageUrl
        return if (imageUrl.startsWith('/')) "${baseUrl.trimEnd('/')}$imageUrl" else imageUrl
    }
}

@Serializable private data class TryOnRequest(val garments: List<TryOnGarment>)
@Serializable private data class TryOnGarment(
    val name: String,
    val category: String,
    val colorHex: String,
    val imageBase64: String? = null,
    val mimeType: String = "image/jpeg",
)
@Serializable private data class TryOnResponse(val imageUrl: String)
@Serializable private data class TryOnError(val message: String)

private fun List<WardrobeItem>.slotOf(item: WardrobeItem): WardrobeSlot = when (item.shape) {
    GarmentShape.Cap -> WardrobeSlot.Head
    GarmentShape.Kurta, GarmentShape.Shirt -> WardrobeSlot.Top
    GarmentShape.Jacket -> WardrobeSlot.Layer
    GarmentShape.Trousers, GarmentShape.Skirt -> WardrobeSlot.Bottom
    GarmentShape.Sneakers, GarmentShape.Sandals -> WardrobeSlot.Shoes
}

private fun Long.toColorHex(): String = "#" + (this and 0xFFFFFF).toString(16).padStart(6, '0').uppercase()
