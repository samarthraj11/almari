package com.almari.shared.feature.home

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin

internal actual fun platformTryOnHttpClient(): HttpClient = HttpClient(Darwin)
internal actual fun platformTryOnBaseUrl(): String = "http://127.0.0.1:8080"
