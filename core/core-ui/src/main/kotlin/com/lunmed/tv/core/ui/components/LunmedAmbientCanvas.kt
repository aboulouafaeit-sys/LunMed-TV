package com.lunmed.tv.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import com.lunmed.tv.core.design.theme.LunmedTheme

/**
 * Root cinematic obsidian canvas with subtle, controlled top ambient lighting.
 */
@Composable
fun LunmedAmbientCanvas(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val colors = LunmedTheme.colors
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.obsidianCanvas)
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(colors.ambientTop, colors.obsidianCanvas),
                    center = Offset(x = 960f, y = 0f),
                    radius = 1200f,
                ),
            ),
        content = content,
    )
}

