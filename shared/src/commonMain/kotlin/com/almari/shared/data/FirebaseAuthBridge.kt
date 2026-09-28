package com.almari.shared.data

import kotlinx.coroutines.flow.MutableStateFlow

data class FirebaseAuthResult(
    val user: FirebaseUserInfo? = null,
    val error: String? = null,
)

data class FirebaseUserInfo(
    val uid: String,
    val email: String,
    val displayName: String,
)

object FirebaseAuthBridge {
    val pending = MutableStateFlow<FirebaseAuthResult?>(null)
    var signOut: (() -> Unit)? = null
    var idTokenProvider: (suspend () -> String?)? = null
    fun clear() { pending.value = null }
}

fun handleFirebaseUser(uid: String, email: String, displayName: String) {
    FirebaseAuthBridge.pending.value = FirebaseAuthResult(
        user = FirebaseUserInfo(uid, email, displayName),
    )
}

fun handleFirebaseAuthError(message: String) {
    FirebaseAuthBridge.pending.value = FirebaseAuthResult(error = message)
}

fun configureFirebaseSignOut(handler: (() -> Unit)?) {
    FirebaseAuthBridge.signOut = handler
}

fun configureFirebaseIdTokenProvider(provider: (suspend () -> String?)?) {
    FirebaseAuthBridge.idTokenProvider = provider
}

suspend fun requestFirebaseIdToken(): String? = FirebaseAuthBridge.idTokenProvider?.invoke()

fun requestFirebaseSignOut() {
    FirebaseAuthBridge.signOut?.invoke()
}
