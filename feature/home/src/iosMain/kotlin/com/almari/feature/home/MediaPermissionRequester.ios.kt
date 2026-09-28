package com.almari.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import platform.Photos.PHAuthorizationStatusAuthorized
import platform.Photos.PHAuthorizationStatusLimited
import platform.Photos.PHAuthorizationStatusNotDetermined
import platform.Photos.PHPhotoLibrary
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

@Composable
internal actual fun rememberMediaPermissionRequester(
    onResult: (permission: MediaPermission, granted: Boolean) -> Unit,
): MediaPermissionRequester = remember(onResult) {
    object : MediaPermissionRequester {
        override fun requestCamera() {
            // The native FileKit camera controller owns the iOS camera authorization prompt.
            // NSCameraUsageDescription is declared in the host application's Info.plist.
            onResultOnMain(onResult, MediaPermission.Camera, true)
        }

        override fun requestGallery() {
            when (PHPhotoLibrary.authorizationStatus()) {
                PHAuthorizationStatusAuthorized, PHAuthorizationStatusLimited ->
                    onResultOnMain(onResult, MediaPermission.Gallery, true)
                PHAuthorizationStatusNotDetermined -> {
                    PHPhotoLibrary.requestAuthorization { status ->
                        onResultOnMain(
                            onResult,
                            MediaPermission.Gallery,
                            status == PHAuthorizationStatusAuthorized || status == PHAuthorizationStatusLimited,
                        )
                    }
                }
                else -> onResultOnMain(onResult, MediaPermission.Gallery, false)
            }
        }
    }
}

private fun onResultOnMain(
    callback: (MediaPermission, Boolean) -> Unit,
    permission: MediaPermission,
    granted: Boolean,
) {
    dispatch_async(dispatch_get_main_queue()) {
        callback(permission, granted)
    }
}
