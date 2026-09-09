package com.almari.shared.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp

actual fun platformHttpClient(): HttpClient = HttpClient(OkHttp)
actual val defaultApiBaseUrl: String = "http://10.0.2.2:8080"
actual val defaultGoogleAuthStartUrl: String = "http://127.0.0.1:8080/auth/google/start"
