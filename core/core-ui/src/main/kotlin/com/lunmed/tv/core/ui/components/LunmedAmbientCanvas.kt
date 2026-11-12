package com.lunmed.tv.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.lunmed.tv.core.design.theme.LunmedTheme
import com.lunmed.tv.core.model.AppInfo
import com.lunmed.tv.core.model.LunmedWallpaper
import com.lunmed.tv.core.model.ScenicType

/**
 * Root cinematic obsidian canvas with adaptive dynamic scenic wallpapers.
 * Renders breathtaking procedural scenic landscapes (Nordic Aurora, Cosmic Nebula,
 * Sahara Sunset, Alpine Mist, Neo Tokyo, Obsidian Silk) on GPU Canvas and harmonizes
 * dynamically with active application focus.
 */
@Composable
fun LunmedAmbientCanvas(
    modifier: Modifier = Modifier,
    wallpaper: LunmedWallpaper? = null,
    focusedApp: AppInfo? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val colors = LunmedTheme.colors
    val anim = LunmedTheme.animation

    val appAura = remember(focusedApp?.packageName, focusedApp?.categoryId) {
        AdaptiveAuraEngine.auraForApp(focusedApp, colors.accentPrimary)
    }

    val baseColor = if (wallpaper != null) {
        Color(wallpaper.primaryColorArgb)
    } else {
        colors.obsidianCanvas
    }

    val targetGlowColor = if (focusedApp != null) {
        appAura.copy(alpha = 0.24f)
    } else if (wallpaper != null) {
        Color(wallpaper.ambientGlowArgb).copy(alpha = 0.22f)
    } else {
        colors.ambientTop
    }

    val targetSecondaryColor = if (focusedApp != null) {
        appAura.copy(alpha = 0.16f)
    } else if (wallpaper != null) {
        Color(wallpaper.secondaryColorArgb).copy(alpha = 0.32f)
    } else {
        colors.obsidianCanvas
    }

    // Buttery smooth Apple TV-grade crossfade between ambient aura states (480ms)
    val auraSpec = remember { tween<Color>(durationMillis = 480, easing = FastOutSlowInEasing) }
    val animatedBase by animateColorAsState(targetValue = baseColor, animationSpec = anim.transitionSpec(), label = "AmbientCanvasBase")
    val animatedGlow by animateColorAsState(targetValue = targetGlowColor, animationSpec = auraSpec, label = "AmbientCanvasGlow")
    val animatedSecondary by animateColorAsState(targetValue = targetSecondaryColor, animationSpec = auraSpec, label = "AmbientCanvasSecondary")

    val scenicType = wallpaper?.scenicType ?: ScenicType.NORDIC_AURORA

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(animatedBase),
    ) {
        // 1. Procedural Scenic Landscape Rendered on GPU Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            when (scenicType) {
                ScenicType.NORDIC_AURORA -> drawNordicAurora(animatedGlow, animatedSecondary)
                ScenicType.COSMIC_NEBULA -> drawCosmicNebula(animatedGlow, animatedSecondary)
                ScenicType.SAHARA_SUNSET -> drawSaharaSunset(animatedGlow, animatedSecondary)
                ScenicType.ALPINE_MIST -> drawAlpineMist(animatedGlow, animatedSecondary)
                ScenicType.TOKYO_CYBER -> drawTokyoCyber(animatedGlow, animatedSecondary)
                ScenicType.OBSIDIAN_SILK -> drawObsidianSilk(animatedGlow, animatedSecondary)
            }
        }

        // 2. Focused App Dynamic Adaptive Bloom (smoothly shifts with remote focus)
        if (focusedApp != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(animatedGlow, Color.Transparent),
                            center = Offset(x = 960f, y = 80f),
                            radius = 1100f,
                        ),
                    ),
            )
        }

        // 3. User Content Layer
        content()
    }
}

private fun DrawScope.drawNordicAurora(glow: Color, secondary: Color) {
    val w = size.width
    val h = size.height

    // Radial upper aura
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(glow.copy(alpha = 0.28f), secondary.copy(alpha = 0.15f), Color.Transparent),
            center = Offset(w * 0.45f, h * 0.1f),
            radius = w * 0.65f,
        ),
    )

    // Aurora Ribbon 1 (Emerald Wave)
    val path1 = Path().apply {
        moveTo(0f, h * 0.20f)
        cubicTo(w * 0.25f, h * 0.08f, w * 0.60f, h * 0.32f, w, h * 0.15f)
        lineTo(w, h * 0.45f)
        cubicTo(w * 0.65f, h * 0.55f, w * 0.30f, h * 0.30f, 0f, h * 0.42f)
        close()
    }
    drawPath(
        path = path1,
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0x0000FF87),
                Color(0x2800FF87),
                Color(0x3560EFFF),
                Color(0x227B2CBF),
                Color(0x007B2CBF),
            ),
            start = Offset(0f, 0f),
            end = Offset(w, h * 0.4f),
        ),
    )

    // Aurora Ribbon 2 (Ethereal Violet Veil)
    val path2 = Path().apply {
        moveTo(0f, h * 0.35f)
        cubicTo(w * 0.35f, h * 0.22f, w * 0.70f, h * 0.45f, w, h * 0.28f)
        lineTo(w, h * 0.52f)
        cubicTo(w * 0.60f, h * 0.62f, w * 0.25f, h * 0.48f, 0f, h * 0.55f)
        close()
    }
    drawPath(
        path = path2,
        brush = Brush.linearGradient(
            colors = listOf(
                Color(0x007B2CBF),
                Color(0x207B2CBF),
                Color(0x2400FF87),
                Color(0x0000FF87),
            ),
            start = Offset(w * 0.2f, h * 0.1f),
            end = Offset(w * 0.8f, h * 0.6f),
        ),
    )

    // Dark Silhouette Mountain Ridge at bottom
    val mountainPath = Path().apply {
        moveTo(0f, h)
        lineTo(0f, h * 0.88f)
        lineTo(w * 0.15f, h * 0.82f)
        lineTo(w * 0.28f, h * 0.87f)
        lineTo(w * 0.48f, h * 0.79f)
        lineTo(w * 0.62f, h * 0.85f)
        lineTo(w * 0.78f, h * 0.76f)
        lineTo(w * 0.90f, h * 0.83f)
        lineTo(w, h * 0.80f)
        lineTo(w, h)
        close()
    }
    drawPath(
        path = mountainPath,
        brush = Brush.verticalGradient(
            colors = listOf(Color(0x4402060C), Color(0x99000204)),
            startY = h * 0.75f,
            endY = h,
        ),
    )
}

private fun DrawScope.drawCosmicNebula(glow: Color, secondary: Color) {
    val w = size.width
    val h = size.height

    // Core Celestial Nebula Cloud
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(Color(0x35FF007F), Color(0x208338EC), Color.Transparent),
            center = Offset(w * 0.65f, h * 0.35f),
            radius = w * 0.55f,
        ),
    )

    // Secondary Amber Stellar Cradle
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(Color(0x22FFB703), Color(0x12FF007F), Color.Transparent),
            center = Offset(w * 0.25f, h * 0.70f),
            radius = w * 0.45f,
        ),
    )

    // Twinkling Starfield Particles
    val starPositions = listOf(
        Offset(w * 0.12f, h * 0.18f),
        Offset(w * 0.22f, h * 0.35f),
        Offset(w * 0.34f, h * 0.12f),
        Offset(w * 0.48f, h * 0.28f),
        Offset(w * 0.58f, h * 0.15f),
        Offset(w * 0.72f, h * 0.22f),
        Offset(w * 0.82f, h * 0.42f),
        Offset(w * 0.91f, h * 0.16f),
        Offset(w * 0.18f, h * 0.65f),
        Offset(w * 0.38f, h * 0.78f),
        Offset(w * 0.75f, h * 0.68f),
        Offset(w * 0.88f, h * 0.82f),
    )
    starPositions.forEachIndexed { i, pos ->
        val radius = if (i % 3 == 0) 2.5f else 1.5f
        val alpha = if (i % 2 == 0) 0.65f else 0.40f
        drawCircle(
            color = Color.White.copy(alpha = alpha),
            radius = radius,
            center = pos,
        )
    }
}

private fun DrawScope.drawSaharaSunset(glow: Color, secondary: Color) {
    val w = size.width
    val h = size.height

    // Warm Twilight Sky Gradient
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0x00FF9100),
                Color(0x22FF5722),
                Color(0x35FF9100),
                Color(0x183E1508),
            ),
            startY = 0f,
            endY = h * 0.75f,
        ),
    )

    // Horizon Sun Halo
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0x40FFC107), Color(0x18FF5722), Color.Transparent),
            center = Offset(w * 0.50f, h * 0.62f),
            radius = w * 0.35f,
        ),
        radius = w * 0.35f,
        center = Offset(w * 0.50f, h * 0.62f),
    )

    // Dunes Ridge 1
    val dune1 = Path().apply {
        moveTo(0f, h)
        lineTo(0f, h * 0.78f)
        cubicTo(w * 0.30f, h * 0.72f, w * 0.60f, h * 0.85f, w, h * 0.74f)
        lineTo(w, h)
        close()
    }
    drawPath(
        path = dune1,
        brush = Brush.verticalGradient(
            colors = listOf(Color(0x55351206), Color(0x99180702)),
            startY = h * 0.70f,
            endY = h,
        ),
    )

    // Dunes Ridge 2 (Overlapping)
    val dune2 = Path().apply {
        moveTo(0f, h)
        lineTo(0f, h * 0.86f)
        cubicTo(w * 0.40f, h * 0.88f, w * 0.75f, h * 0.78f, w, h * 0.84f)
        lineTo(w, h)
        close()
    }
    drawPath(
        path = dune2,
        brush = Brush.verticalGradient(
            colors = listOf(Color(0x77280C04), Color(0xCC0E0301)),
            startY = h * 0.78f,
            endY = h,
        ),
    )
}

private fun DrawScope.drawAlpineMist(glow: Color, secondary: Color) {
    val w = size.width
    val h = size.height

    // Atmospheric Cold Sky Radial
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(Color(0x2880D8FF), Color(0x122979FF), Color.Transparent),
            center = Offset(w * 0.5f, h * 0.2f),
            radius = w * 0.65f,
        ),
    )

    // Monumental Alpine Peaks
    val peaks = Path().apply {
        moveTo(0f, h)
        lineTo(0f, h * 0.72f)
        lineTo(w * 0.18f, h * 0.58f)
        lineTo(w * 0.34f, h * 0.68f)
        lineTo(w * 0.52f, h * 0.52f)
        lineTo(w * 0.70f, h * 0.65f)
        lineTo(w * 0.86f, h * 0.55f)
        lineTo(w, h * 0.66f)
        lineTo(w, h)
        close()
    }
    drawPath(
        path = peaks,
        brush = Brush.verticalGradient(
            colors = listOf(Color(0x400C1E30), Color(0x90050B14)),
            startY = h * 0.50f,
            endY = h,
        ),
    )

    // Valley Mist Fog Band
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color.Transparent, Color(0x2580D8FF), Color(0x1080D8FF), Color.Transparent),
            startY = h * 0.62f,
            endY = h * 0.82f,
        ),
    )
}

private fun DrawScope.drawTokyoCyber(glow: Color, secondary: Color) {
    val w = size.width
    val h = size.height

    // Nocturnal Cyberpunk Skyline Horizon
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(Color(0x3500E5FF), Color(0x22F72585), Color.Transparent),
            center = Offset(w * 0.50f, h * 0.55f),
            radius = w * 0.60f,
        ),
    )

    // Cyber Electric Horizon Line
    drawLine(
        brush = Brush.horizontalGradient(
            colors = listOf(Color.Transparent, Color(0x6600E5FF), Color(0x88F72585), Color(0x6600E5FF), Color.Transparent),
        ),
        start = Offset(0f, h * 0.65f),
        end = Offset(w, h * 0.65f),
        strokeWidth = 2.5f,
    )

    // Vertical Cyber Light Pillars
    val pillarX = listOf(w * 0.22f, w * 0.38f, w * 0.64f, w * 0.82f)
    pillarX.forEachIndexed { i, px ->
        val color = if (i % 2 == 0) Color(0x1800E5FF) else Color(0x18F72585)
        drawLine(
            brush = Brush.verticalGradient(
                colors = listOf(Color.Transparent, color, Color.Transparent),
                startY = h * 0.35f,
                endY = h * 0.65f,
            ),
            start = Offset(px, h * 0.35f),
            end = Offset(px, h * 0.65f),
            strokeWidth = 3f,
        )
    }
}

private fun DrawScope.drawObsidianSilk(glow: Color, secondary: Color) {
    val w = size.width
    val h = size.height

    // 24K Champagne Gold Atmospheric Depth
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(glow.copy(alpha = 0.25f), secondary.copy(alpha = 0.15f), Color.Transparent),
            center = Offset(w * 0.50f, h * 0.15f),
            radius = w * 0.65f,
        ),
    )

    // Diagonal Liquid Silk Sheen Wave
    val silkWave = Path().apply {
        moveTo(0f, h * 0.30f)
        cubicTo(w * 0.35f, h * 0.20f, w * 0.65f, h * 0.45f, w, h * 0.35f)
        lineTo(w, h * 0.55f)
        cubicTo(w * 0.65f, h * 0.65f, w * 0.35f, h * 0.40f, 0f, h * 0.50f)
        close()
    }
    drawPath(
        path = silkWave,
        brush = Brush.linearGradient(
            colors = listOf(
                Color.Transparent,
                Color(0x1CD4AF37),
                Color(0x28FFF8E7),
                Color(0x1CD4AF37),
                Color.Transparent,
            ),
            start = Offset(0f, h * 0.25f),
            end = Offset(w, h * 0.55f),
        ),
    )
}
