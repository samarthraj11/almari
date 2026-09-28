package com.almari.feature.home

import androidx.compose.runtime.Composable

internal enum class MediaPermission {
    Camera,
    Gallery,
}

internal interface MediaPermissionRequester {
    fun requestCamera()
    fun requestGallery()
}

@Composable
internal expect fun rememberMediaPermissionRequester(
    onResult: (permission: MediaPermission, granted: Boolean) -> Unit,
): MediaPermissionRequester
