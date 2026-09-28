package com.almari.feature.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
internal actual fun rememberMediaPermissionRequester(
    onResult: (permission: MediaPermission, granted: Boolean) -> Unit,
): MediaPermissionRequester {
    val context = LocalContext.current
    val latestOnResult = rememberUpdatedState(onResult)
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        latestOnResult.value(MediaPermission.Camera, granted)
    }
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        latestOnResult.value(MediaPermission.Gallery, granted)
    }

    return remember(context, cameraLauncher, galleryLauncher) {
        object : MediaPermissionRequester {
            override fun requestCamera() {
                val permission = Manifest.permission.CAMERA
                if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                    latestOnResult.value(MediaPermission.Camera, true)
                } else {
                    cameraLauncher.launch(permission)
                }
            }

            override fun requestGallery() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // Android's system photo picker grants access only to the image the user selects.
                    latestOnResult.value(MediaPermission.Gallery, true)
                    return
                }
                val permission = Manifest.permission.READ_EXTERNAL_STORAGE
                if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                    latestOnResult.value(MediaPermission.Gallery, true)
                } else {
                    galleryLauncher.launch(permission)
                }
            }
        }
    }
}
