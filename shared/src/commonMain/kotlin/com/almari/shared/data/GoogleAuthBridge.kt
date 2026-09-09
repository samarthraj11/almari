package com.almari.shared.data

import kotlinx.coroutines.flow.MutableStateFlow

data class GoogleAuthRedirect(val code: String? = null, val error: String? = null)

object GoogleAuthBridge {
    val pending = MutableStateFlow<GoogleAuthRedirect?>(null)
    fun clear() { pending.value = null }
}

fun handleGoogleAuthRedirect(url: String) {
    if (!url.startsWith("almari://auth")) return
    val values = url.substringAfter('?', "")
        .split('&')
        .mapNotNull { part -> part.split('=', limit = 2).takeIf { it.size == 2 } }
        .associate { it[0] to it[1] }
    GoogleAuthBridge.pending.value = GoogleAuthRedirect(
        code = values["code"],
        error = values["error"],
    )
}
