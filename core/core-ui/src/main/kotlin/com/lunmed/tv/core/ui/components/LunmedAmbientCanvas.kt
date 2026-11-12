package com.lunmed.tv.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.lunmed.tv.core.design.theme.LunmedTheme
import com.lunmed.tv.core.model.LunmedWallpaper

/**
 * Root cinematic obsidian canvas with subtle, dynamic ambient lighting adapted
 * to the currently active luxury wallpaper profile.
 */
@Composable
fun LunmedAmbientCanvas(
    modifier: Modifier = Modifier,
    wallpaper: LunmedWallpaper? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val colors = LunmedTheme.colors
    val anim = LunmedTheme.animation

    val baseColor = if (wallpaper != null) {
        Color(wallpaper.primaryColorArgb)
    } else {
        colors.obsidianCanvas
    }

    val glowColor = if (wallpaper != null) {
        Color(wallpaper.ambientGlowArgb).copy(alpha = 0.22f)
    } else {
        colors.ambientTop
    }

    val secondaryColor = if (wallpaper != null) {
        Color(wallpaper.secondaryColorArgb)
    } else {
        colors.obsidianCanvas
    }

    val animatedBase by animateColorAsState(targetValue = baseColor, animationSpec = anim.transitionSpec(), label = "AmbientCanvasBase")
    val animatedGlow by animateColorAsState(targetValue = glowColor, animationSpec = anim.transitionSpec(), label = "AmbientCanvasGlow")
    val animatedSecondary by animateColorAsState(targetValue = secondaryColor, animationSpec = anim.transitionSpec(), label = "AmbientCanvasSecondary")

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(animatedBase)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(animatedGlow, Color.Transparent),
                    center = Offset(x = 960f, y = 0f),
                    radius = 1200f,
                ),
            )
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(animatedSecondary.copy(alpha = 0.45f), Color.Transparent),
                    center = Offset(x = 1800f, y = 1000f),
                    radius = 900f,
                ),
            ),
        content = content,
    )
}

