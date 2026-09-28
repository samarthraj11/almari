package com.almari.feature.home

import almari.feature.home.generated.resources.Res
import almari.feature.home.generated.resources.garment_black_bag
import almari.feature.home.generated.resources.garment_cream_top
import almari.feature.home.generated.resources.garment_denim_skirt
import almari.feature.home.generated.resources.garment_pink_cardigan_large
import almari.feature.home.generated.resources.garment_pink_cardigan_small
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowUpward
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FlashOn
import androidx.compose.material.icons.rounded.PhotoLibrary
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.almari.core.designsystem.LocalAlmariColors
import com.almari.feature.home.components.StudioChip
import com.almari.shared.feature.home.GarmentShape
import com.almari.shared.feature.home.HomeComponent
import com.almari.shared.feature.home.WardrobeSlot
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openCameraPicker
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.extension
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

private data class CapturedPiece(val bytes: ByteArray, val mimeType: String)

@Composable
internal fun CaptureFlowScreen(component: HomeComponent) {
    var capture by remember { mutableStateOf<CapturedPiece?>(null) }
    var reviewing by remember { mutableStateOf(false) }

    if (reviewing && capture != null) {
        ReviewDeckScreen(
            capture = capture!!,
            index = 1,
            total = 1,
            onClose = { reviewing = false },
            onConfirm = { slot, shape, color ->
                component.addGarment(
                    name = "New " + slot.label.lowercase(),
                    brand = "Almari capture",
                    slot = slot,
                    shape = shape,
                    colorValue = color,
                    imageData = capture!!.bytes,
                    imageMimeType = capture!!.mimeType,
                )
            },
        )
    } else {
        BatchCaptureScreen(
            captured = capture,
            onCaptured = {
                capture = it
                reviewing = true
            },
            onClose = { component.showAddGarment(false) },
        )
    }
}

@Composable
private fun BatchCaptureScreen(
    captured: CapturedPiece?,
    onCaptured: (CapturedPiece) -> Unit,
    onClose: () -> Unit,
) {
    val colors = LocalAlmariColors.current
    val scope = rememberCoroutineScope()
    var permissionMessage by remember { mutableStateOf<String?>(null) }
    val openCameraPicker = {
        scope.launch {
            FileKit.openCameraPicker()?.let { file ->
                onCaptured(CapturedPiece(file.readBytes(), captureMimeType(file.extension)))
            }
        }
    }
    val openGalleryPicker = {
        scope.launch {
            FileKit.openFilePicker(type = FileKitType.Image)?.let { file ->
                onCaptured(CapturedPiece(file.readBytes(), captureMimeType(file.extension)))
            }
        }
    }
    val permissionRequester = rememberMediaPermissionRequester { permission, granted ->
        if (granted) {
            permissionMessage = null
            when (permission) {
                MediaPermission.Camera -> openCameraPicker()
                MediaPermission.Gallery -> openGalleryPicker()
            }
        } else {
            permissionMessage = when (permission) {
                MediaPermission.Camera -> "Camera access is needed to photograph a garment."
                MediaPermission.Gallery -> "Photo access is needed to choose an existing garment image."
            }
        }
    }
    LaunchedEffect(Unit) {
        permissionRequester.requestCamera()
    }
    val backdrop = Brush.radialGradient(
        colors = listOf(colors.softPink, colors.softLilac, colors.studioBackground),
        radius = 900f,
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.studioBackground)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
                .padding(14.dp)
                .clip(RoundedCornerShape(30.dp))
                .background(backdrop),
        )

        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onClose) {
                    Icon(Icons.Rounded.Close, "Close capture", tint = colors.studioInk)
                }
                Icon(Icons.Rounded.FlashOn, "Flash", tint = colors.studioInk)
                Icon(Icons.Rounded.Refresh, "Switch camera", tint = colors.studioInk)
            }

            Spacer(Modifier.height(18.dp))
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center,
            ) {
                if (captured == null) {
                    Image(
                        painter = painterResource(Res.drawable.garment_pink_cardigan_large),
                        contentDescription = "Garment framing preview",
                        modifier = Modifier.fillMaxSize().padding(20.dp),
                        contentScale = ContentScale.Fit,
                    )
                } else {
                    AsyncImage(
                        model = captured.bytes,
                        contentDescription = "Captured garment",
                        modifier = Modifier.fillMaxSize().padding(18.dp),
                        contentScale = ContentScale.Fit,
                    )
                }
            }

            Text(
                text = if (captured == null) "0 / 30" else "1 / 30",
                color = colors.studioInk,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp),
            )
            permissionMessage?.let { message ->
                Text(
                    text = message,
                    color = Color(0xFFB42318),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(7.dp),
            ) {
                listOf(
                    Res.drawable.garment_pink_cardigan_small,
                    Res.drawable.garment_cream_top,
                    Res.drawable.garment_denim_skirt,
                    Res.drawable.garment_black_bag,
                ).forEach { drawable ->
                    CaptureThumbnail(drawable, Modifier.weight(1f))
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(
                    onClick = {
                        permissionMessage = null
                        permissionRequester.requestGallery()
                    },
                    modifier = Modifier.size(54.dp).background(Color.White, CircleShape),
                ) {
                    Icon(Icons.Rounded.PhotoLibrary, "Choose from gallery", tint = colors.studioInk)
                }

                Box(
                    modifier = Modifier
                        .size(82.dp)
                        .background(colors.studioInk, CircleShape)
                        .padding(6.dp)
                        .border(4.dp, Color.White, CircleShape)
                        .clickable {
                            permissionMessage = null
                            permissionRequester.requestCamera()
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Box(Modifier.size(56.dp).background(Color.White, CircleShape))
                }

                Spacer(Modifier.size(54.dp))
            }
        }
    }
}

@Composable
private fun CaptureThumbnail(drawable: DrawableResource, modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(drawable),
        contentDescription = null,
        modifier = modifier
            .height(68.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(Color.White)
            .padding(5.dp),
        contentScale = ContentScale.Fit,
    )
}

@Composable
private fun ReviewDeckScreen(
    capture: CapturedPiece,
    index: Int,
    total: Int,
    onClose: () -> Unit,
    onConfirm: (WardrobeSlot, GarmentShape, Long) -> Unit,
) {
    ReviewDeckScreenContent(capture, index, total, onClose, onConfirm)
}

@Composable
private fun ReviewDeckScreenContent(
    capture: CapturedPiece,
    index: Int,
    total: Int,
    onClose: () -> Unit,
    onConfirm: (WardrobeSlot, GarmentShape, Long) -> Unit,
) {
    val colors = LocalAlmariColors.current
    var slot by remember { mutableStateOf(WardrobeSlot.Top) }
    var selectedColor by remember { mutableStateOf(0xFFF5B6CEL) }
    var formality by remember { mutableStateOf("Casual") }
    val palette = listOf(
        0xFFF5B6CEL to Color(0xFFF5B6CE),
        0xFFE8DDD0L to Color(0xFFE8DDD0),
        0xFFC7B4D8L to Color(0xFFC7B4D8),
        0xFFE9ECEFL to Color(0xFFE9ECEF),
        0xFF1A1A1AL to Color(0xFF1A1A1A),
        0xFF7F8763L to Color(0xFF7F8763),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.studioBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 17.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onClose) { Icon(Icons.Rounded.Close, "Back to camera", tint = colors.studioInk) }
            Text(index.toString() + " / " + total, color = colors.studioInk, fontWeight = FontWeight.Bold)
            Spacer(Modifier.size(48.dp))
        }

        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            repeat(6) { dot ->
                Box(
                    Modifier
                        .size(if (dot == 1) 10.dp else 8.dp)
                        .background(if (dot < 2) colors.electricBlue else Color(0xFFDADADA), CircleShape),
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(270.dp)
                .background(
                    Brush.radialGradient(listOf(colors.softPink, colors.softLilac, colors.studioBackground)),
                    RoundedCornerShape(26.dp),
                ),
            contentAlignment = Alignment.Center,
        ) {
            AsyncImage(
                model = capture.bytes,
                contentDescription = "Garment under review",
                modifier = Modifier.fillMaxSize().padding(18.dp),
                contentScale = ContentScale.Fit,
            )
        }

        ReviewLabel("CATEGORY")
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(7.dp),
        ) {
            listOf(WardrobeSlot.Top, WardrobeSlot.Layer, WardrobeSlot.Bottom, WardrobeSlot.Shoes).forEach { option ->
                StudioChip(option.label.lowercase().replaceFirstChar { it.uppercase() }, slot == option) {
                    slot = option
                }
            }
        }

        ReviewLabel("COLOR")
        Row(horizontalArrangement = Arrangement.spacedBy(11.dp), verticalAlignment = Alignment.CenterVertically) {
            palette.forEach { (value, swatch) ->
                Box(
                    modifier = Modifier
                        .size(if (selectedColor == value) 38.dp else 34.dp)
                        .background(swatch, CircleShape)
                        .then(
                            if (selectedColor == value) Modifier.border(2.dp, colors.studioInk, CircleShape)
                            else Modifier,
                        )
                        .clickable { selectedColor = value },
                )
            }
        }

        ReviewLabel("FORMALITY")
        Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
            listOf("Casual", "Smart", "Formal").forEach { option ->
                StudioChip(option, formality == option) { formality = option }
            }
        }

        Spacer(Modifier.weight(1f))
        Column(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clickable {
                    onConfirm(slot, shapeForSlot(slot), selectedColor)
                }
                .padding(horizontal = 42.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(Icons.Rounded.ArrowUpward, null, tint = colors.studioInk, modifier = Modifier.size(36.dp))
            Text("SWIPE UP", color = colors.studioInk, fontSize = 20.sp, fontWeight = FontWeight.Black)
            Text("to confirm & add", color = colors.studioMuted, fontSize = 12.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun ReviewLabel(text: String) {
    Text(
        text = text,
        color = LocalAlmariColors.current.studioInk,
        fontSize = 11.sp,
        fontWeight = FontWeight.Black,
        modifier = Modifier.padding(top = 12.dp, bottom = 6.dp),
    )
}

private fun shapeForSlot(slot: WardrobeSlot): GarmentShape = when (slot) {
    WardrobeSlot.Head -> GarmentShape.Cap
    WardrobeSlot.Top -> GarmentShape.Shirt
    WardrobeSlot.Layer -> GarmentShape.Jacket
    WardrobeSlot.Bottom -> GarmentShape.Trousers
    WardrobeSlot.Shoes -> GarmentShape.Sneakers
}

private fun captureMimeType(extension: String): String = when (extension.lowercase()) {
    "png" -> "image/png"
    "webp" -> "image/webp"
    else -> "image/jpeg"
}
