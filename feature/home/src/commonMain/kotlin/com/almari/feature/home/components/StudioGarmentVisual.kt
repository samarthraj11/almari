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
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    } else {
        Image(
            painter = painterResource(studioDrawable(item)),
            contentDescription = item.name,
            contentScale = contentScale,
            modifier = modifier.padding(4.dp),
        )
    }
}

private fun studioDrawable(item: WardrobeItem): DrawableResource = when (item.id) {
    "head-indigo", "head-olive" -> Res.drawable.garment_pink_beret
    "top-kurta", "top-sage" -> Res.drawable.garment_cream_top
    "top-shirt" -> Res.drawable.garment_pink_cardigan_small
    "layer-brown", "layer-ink" -> Res.drawable.garment_pink_cardigan_large
    "layer-denim" -> Res.drawable.garment_pink_cardigan_small
    "bottom-denim", "bottom-skirt" -> Res.drawable.garment_denim_skirt
    "bottom-black" -> Res.drawable.garment_denim_skirt
    "shoes-street" -> Res.drawable.garment_white_sneakers
    "shoes-blue" -> Res.drawable.garment_pink_heels
    "shoes-tan" -> Res.drawable.garment_black_boots
    else -> when (item.shape) {
        GarmentShape.Cap -> Res.drawable.garment_pink_beret
        GarmentShape.Kurta, GarmentShape.Shirt -> Res.drawable.garment_cream_top
        GarmentShape.Jacket -> Res.drawable.garment_pink_cardigan_large
        GarmentShape.Trousers, GarmentShape.Skirt -> Res.drawable.garment_denim_skirt
        GarmentShape.Sneakers -> Res.drawable.garment_white_sneakers
        GarmentShape.Sandals -> Res.drawable.garment_pink_heels
    }
}
