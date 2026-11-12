package com.lunmed.tv.feature.device.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.lunmed.tv.core.design.theme.LunmedTheme
import com.lunmed.tv.core.model.LunmedWallpaper

@Composable
fun WallpaperCard(
    wallpaper: LunmedWallpaper,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LunmedTheme.colors
    val anim = LunmedTheme.animation
    val spacing = LunmedTheme.spacing
    var isFocused by remember { mutableStateOf(false) }

    val containerColor by animateColorAsState(
        targetValue = when {
            isFocused -> colors.surfaceCardFocused
            isSelected -> colors.surfaceElevated
            else -> colors.surfaceCard
        },
        animationSpec = anim.focusSpec(),
        label = "WallpaperCardBg",
    )

    val primaryCol = Color(wallpaper.primaryColorArgb)
    val glowCol = Color(wallpaper.ambientGlowArgb)
    val secondaryCol = Color(wallpaper.secondaryColorArgb)

    Surface(
        onClick = onSelect,
        shape = ClickableSurfaceDefaults.shape(LunmedTheme.shapes.cardMedium),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = containerColor,
            focusedContainerColor = colors.surfaceCardFocused,
        ),
        border = ClickableSurfaceDefaults.border(
            border = if (isSelected) {
                Border(border = BorderStroke(1.5.dp, colors.accentPrimary), shape = LunmedTheme.shapes.cardMedium)
            } else {
                Border(border = BorderStroke(1.dp, colors.borderSubtle), shape = LunmedTheme.shapes.cardMedium)
            },
            focusedBorder = Border(
                border = BorderStroke(2.dp, colors.borderFocused),
                shape = LunmedTheme.shapes.cardMedium,
            ),
        ),
        modifier = modifier.onFocusChanged { isFocused = it.isFocused },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.md),
            verticalArrangement = Arrangement.spacedBy(spacing.sm),
        ) {
            // Live Preview Thumbnail Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .background(primaryCol, LunmedTheme.shapes.cardMedium)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(glowCol.copy(alpha = 0.5f), Color.Transparent),
                            center = Offset(200f, 0f),
                            radius = 300f,
                        ),
                        shape = LunmedTheme.shapes.cardMedium,
                    )
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(secondaryCol.copy(alpha = 0.7f), Color.Transparent),
                            center = Offset(600f, 250f),
                            radius = 280f,
                        ),
                        shape = LunmedTheme.shapes.cardMedium,
                    )
                    .padding(spacing.sm),
            ) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .background(colors.accentPrimary, LunmedTheme.shapes.pill)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = "Active",
                                tint = colors.obsidianCanvas,
                                modifier = Modifier.size(12.dp),
                            )
                            Text(
                                text = "ACTIVE",
                                style = LunmedTheme.typography.labelMeta.copy(fontWeight = FontWeight.Bold),
                                color = colors.obsidianCanvas,
                            )
                        }
                    }
                }
            }

            // Info Section
            Text(
                text = wallpaper.title,
                style = LunmedTheme.typography.titleCard,
                color = colors.textPrimary,
            )
            Text(
                text = wallpaper.description,
                style = LunmedTheme.typography.labelMeta,
                color = colors.textSecondary,
                maxLines = 2,
            )
        }
    }
}

