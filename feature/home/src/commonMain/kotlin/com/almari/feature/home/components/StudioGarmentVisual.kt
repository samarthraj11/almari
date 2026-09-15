package com.almari.feature.home.components

import almari.feature.home.generated.resources.Res
import almari.feature.home.generated.resources.garment_black_boots
import almari.feature.home.generated.resources.garment_cream_top
import almari.feature.home.generated.resources.garment_denim_skirt
import almari.feature.home.generated.resources.garment_pink_beret
import almari.feature.home.generated.resources.garment_pink_cardigan_large
import almari.feature.home.generated.resources.garment_pink_cardigan_small
import almari.feature.home.generated.resources.garment_pink_heels
import almari.feature.home.generated.resources.garment_white_sneakers
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.almari.shared.feature.home.GarmentShape
import com.almari.shared.feature.home.WardrobeItem
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

@Composable
internal fun StudioGarmentVisual(
    item: WardrobeItem,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
) {
    val suppliedImage = item.imageData ?: item.imageUrl
    if (suppliedImage != null) {
        AsyncImage(
            model = suppliedImage,
            contentDescription = item.name,
            contentScale = contentScale,
            modifier = modifier.padding(6.dp),
        )
    } else if (studioDrawable(item) != null) {
        Image(
            painter = painterResource(studioDrawable(item)!!),
            contentDescription = item.name,
            contentScale = contentScale,
            modifier = modifier.padding(4.dp),
        )
    } else {
        Box(modifier = modifier.padding(7.dp)) {
            GarmentIllustration(item.shape, Color(item.colorValue), Modifier.matchParentSize())
        }
    }
}

private fun studioDrawable(item: WardrobeItem): DrawableResource? = when (item.id) {
    "head-beret" -> Res.drawable.garment_pink_beret
    "top-cream" -> Res.drawable.garment_cream_top
    "top-blush" -> Res.drawable.garment_pink_cardigan_small
    "layer-pink" -> Res.drawable.garment_pink_cardigan_large
    "bottom-denim-skirt" -> Res.drawable.garment_denim_skirt
    "shoes-white" -> Res.drawable.garment_white_sneakers
    "shoes-pink" -> Res.drawable.garment_pink_heels
    "shoes-black" -> Res.drawable.garment_black_boots
    else -> null
}
