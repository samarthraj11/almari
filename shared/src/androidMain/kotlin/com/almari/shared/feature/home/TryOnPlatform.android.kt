package com.almari.shared.feature.home

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp

internal actual fun platformTryOnHttpClient(): HttpClient = HttpClient(OkHttp)
internal actual fun platformTryOnBaseUrl(): String = "http://10.0.2.2:8080"
