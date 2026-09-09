package com.almari.feature.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.almari.core.designsystem.LocalAlmariColors
import com.almari.core.designsystem.LocalAlmariTypography
import com.almari.core.designsystem.components.AlmariLogo
import com.almari.feature.home.components.GarmentIllustration
import com.almari.shared.feature.home.GarmentShape

@Composable
fun OnboardingScreen(
    isLoading: Boolean,
    error: String?,
    onContinueWithGoogle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LocalAlmariColors.current
    Column(
        modifier = modifier.fillMaxSize().background(colors.white).statusBarsPadding()
            .verticalScroll(rememberScrollState()).padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(Modifier.fillMaxWidth().padding(top = 12.dp, bottom = 18.dp), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AlmariLogo(size = 30.dp)
                Spacer(Modifier.width(8.dp))
                Text("ALMARI.", color = colors.ink, style = LocalAlmariTypography.current.brand)
            }
            Text("EARLY ACCESS", color = colors.cobalt, style = LocalAlmariTypography.current.caption, modifier = Modifier.background(colors.cobaltSoft, CircleShape).padding(horizontal = 11.dp, vertical = 7.dp))
        }

        WardrobeHero()

        Row(Modifier.padding(top = 13.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FeaturePill(Icons.Rounded.AutoAwesome, "Instant cutout")
            FeaturePill(Icons.Rounded.Lock, "Smart locks")
        }

        Text("Your closet, finally in play.", color = colors.ink, textAlign = TextAlign.Center, style = LocalAlmariTypography.current.display, modifier = Modifier.padding(top = 22.dp))
        Text(
            "Photograph what you own, mix complete looks, and save the combinations that feel like you.",
            color = colors.muted,
            textAlign = TextAlign.Center,
            style = LocalAlmariTypography.current.body,
            modifier = Modifier.padding(top = 9.dp).widthIn(max = 330.dp),
        )

        error?.let {
            Text(it, color = Color(0xFFBA1A1A), textAlign = TextAlign.Center, style = LocalAlmariTypography.current.caption, modifier = Modifier.padding(top = 12.dp))
        }

        OutlinedButton(
            onClick = onContinueWithGoogle,
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth().padding(top = 20.dp).height(54.dp).shadow(2.dp, CircleShape),
            shape = CircleShape,
            colors = ButtonDefaults.outlinedButtonColors(containerColor = colors.white, contentColor = colors.ink),
            border = androidx.compose.foundation.BorderStroke(1.dp, colors.border),
        ) {
            if (isLoading) CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
            else {
                GoogleMark()
                Spacer(Modifier.width(11.dp))
                Text("Continue with Google", fontWeight = FontWeight.SemiBold, style = LocalAlmariTypography.current.body)
            }
        }
        Text("By continuing, you agree to Almari's Terms and Privacy Policy.", color = colors.muted, textAlign = TextAlign.Center, style = LocalAlmariTypography.current.caption, modifier = Modifier.padding(top = 11.dp, bottom = 24.dp))
    }
}

@Composable
private fun WardrobeHero() {
    val colors = LocalAlmariColors.current
    Box(
        Modifier.fillMaxWidth().aspectRatio(1.12f).background(colors.cloud, RoundedCornerShape(32.dp))
            .border(1.dp, colors.border, RoundedCornerShape(32.dp)).padding(18.dp),
    ) {
        Box(Modifier.align(Alignment.TopStart).size(104.dp).background(colors.white, RoundedCornerShape(22.dp)), Alignment.Center) {
            GarmentIllustration(GarmentShape.Shirt, colors.cobalt, Modifier.fillMaxSize().padding(16.dp))
        }
        Box(Modifier.align(Alignment.TopEnd).width(150.dp).height(176.dp).background(colors.cobaltSoft, RoundedCornerShape(26.dp)), Alignment.Center) {
            GarmentIllustration(GarmentShape.Jacket, Color(0xFF7A4D34), Modifier.fillMaxSize().padding(20.dp))
        }
        Box(Modifier.align(Alignment.BottomStart).width(128.dp).height(142.dp).background(colors.white, RoundedCornerShape(24.dp)), Alignment.Center) {
            GarmentIllustration(GarmentShape.Trousers, Color(0xFF456A9E), Modifier.fillMaxSize().padding(18.dp))
        }
        Box(Modifier.align(Alignment.BottomEnd).width(126.dp).height(88.dp).background(colors.white, RoundedCornerShape(22.dp)), Alignment.Center) {
            GarmentIllustration(GarmentShape.Sneakers, colors.ink, Modifier.fillMaxSize().padding(15.dp))
        }
        Row(Modifier.align(Alignment.BottomCenter).padding(bottom = 7.dp).background(colors.ink, CircleShape).padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(7.dp).background(colors.lime, CircleShape))
            Spacer(Modifier.width(7.dp))
            Text("5-slot studio ready", color = colors.white, style = LocalAlmariTypography.current.caption)
        }
    }
}

@Composable
private fun FeaturePill(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    val colors = LocalAlmariColors.current
    Row(Modifier.background(colors.cloud, CircleShape).border(1.dp, colors.border, CircleShape).padding(horizontal = 12.dp, vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = colors.cobalt, modifier = Modifier.size(15.dp))
        Spacer(Modifier.width(5.dp))
        Text(text, color = colors.muted, style = LocalAlmariTypography.current.caption)
    }
}

@Composable
private fun GoogleMark() {
    Canvas(Modifier.size(20.dp)) {
        val stroke = Stroke(width = size.minDimension * .18f, cap = StrokeCap.Square)
        drawArc(Color(0xFF4285F4), -40f, 130f, false, style = stroke)
        drawArc(Color(0xFF34A853), 90f, 75f, false, style = stroke)
        drawArc(Color(0xFFFBBC05), 165f, 65f, false, style = stroke)
        drawArc(Color(0xFFEA4335), 230f, 90f, false, style = stroke)
        drawLine(Color(0xFF4285F4), Offset(size.width * .53f, size.height * .52f), Offset(size.width * .94f, size.height * .52f), strokeWidth = size.minDimension * .18f)
    }
}
