package com.almi.shared.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin

actual fun platformHttpClient(): HttpClient = HttpClient(Darwin)
actual val defaultApiBaseUrl: String = "http://127.0.0.1:8080"
actual val defaultGoogleAuthStartUrl: String = "$defaultApiBaseUrl/auth/google/start"
