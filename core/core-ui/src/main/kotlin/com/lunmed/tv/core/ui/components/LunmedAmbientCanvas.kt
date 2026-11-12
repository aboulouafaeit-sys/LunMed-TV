package com.lunmed.tv.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
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
import com.lunmed.tv.core.design.theme.LunmedTheme
import com.lunmed.tv.core.model.AppInfo
import com.lunmed.tv.core.model.LunmedWallpaper

/**
 * Root cinematic obsidian canvas with adaptive dynamic ambient lighting.
 * Seamlessly crossfades ambient lighting to harmonize with both active luxury wallpaper
 * and the currently focused application on the TV screen.
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
        appAura.copy(alpha = 0.22f)
    } else if (wallpaper != null) {
        Color(wallpaper.ambientGlowArgb).copy(alpha = 0.18f)
    } else {
        colors.ambientTop
    }

    val targetSecondaryColor = if (focusedApp != null) {
        appAura.copy(alpha = 0.14f)
    } else if (wallpaper != null) {
        Color(wallpaper.secondaryColorArgb).copy(alpha = 0.28f)
    } else {
        colors.obsidianCanvas
    }

    // Buttery smooth Apple TV-grade crossfade between ambient aura states (480ms)
    val auraSpec = remember { tween<Color>(durationMillis = 480, easing = FastOutSlowInEasing) }
    val animatedBase by animateColorAsState(targetValue = baseColor, animationSpec = anim.transitionSpec(), label = "AmbientCanvasBase")
    val animatedGlow by animateColorAsState(targetValue = targetGlowColor, animationSpec = auraSpec, label = "AmbientCanvasGlow")
    val animatedSecondary by animateColorAsState(targetValue = targetSecondaryColor, animationSpec = auraSpec, label = "AmbientCanvasSecondary")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(animatedBase)
            // Primary top-center ambient aura
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(animatedGlow, Color.Transparent),
                    center = Offset(x = 960f, y = 50f),
                    radius = 1100f,
                ),
            )
            // Secondary bottom-right atmospheric depth glow
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(animatedSecondary, Color.Transparent),
                    center = Offset(x = 1850f, y = 950f),
                    radius = 950f,
                ),
            ),
        content = content,
    )
}
