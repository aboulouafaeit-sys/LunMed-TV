package com.lunmed.tv.feature.device.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Icon
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.lunmed.tv.core.design.theme.LunmedTheme
import com.lunmed.tv.core.model.LunmedWallpaper
import com.lunmed.tv.core.model.ScenicType

/**
 * World-class Scenic Wallpaper Card with procedural GPU vector canvas rendering.
 * Replaces static flat color squares with live mathematical landscape miniature previews.
 */
@Composable
fun WallpaperCard(
    wallpaper: LunmedWallpaper,
    isSelected: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LunmedTheme.colors
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) 1.04f else 1.0f,
        animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
        label = "WallpaperCardScale",
    )

    val containerColor by animateColorAsState(
        targetValue = when {
            isFocused -> Color(0xF9161A28)
            isSelected -> Color(0xF4141824)
            else -> Color(0xF410121C)
        },
        animationSpec = tween(durationMillis = 280),
        label = "WallpaperCardBg",
    )

    val goldRimBrush = remember(colors.accentPrimary) {
        Brush.linearGradient(
            listOf(
                Color(0xFFFFF8E7),
                colors.accentPrimary,
                Color(0xFFC49A45),
            ),
        )
    }

    Surface(
        onClick = onSelect,
        shape = ClickableSurfaceDefaults.shape(RoundedCornerShape(20.dp)),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = containerColor,
            focusedContainerColor = Color(0xF9161A28),
        ),
        border = ClickableSurfaceDefaults.border(
            border = if (isSelected) {
                Border(border = BorderStroke(2.dp, colors.accentPrimary), shape = RoundedCornerShape(20.dp))
            } else {
                Border(border = BorderStroke(1.dp, Color(0x25FFFFFF)), shape = RoundedCornerShape(20.dp))
            },
            focusedBorder = Border(
                border = BorderStroke(2.dp, goldRimBrush),
                shape = RoundedCornerShape(20.dp),
            ),
        ),
        modifier = modifier
            .scale(scale)
            .onFocusChanged { isFocused = it.isFocused },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // Live GPU Procedural Scenic Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(wallpaper.primaryColorArgb)),
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    when (wallpaper.scenicType) {
                        ScenicType.NORDIC_AURORA -> {
                            drawRect(
                                Brush.verticalGradient(listOf(Color(0xFF030A14), Color(0xFF0A1D2D))),
                            )
                            val ribbonPath = Path().apply {
                                moveTo(0f, h * 0.7f)
                                cubicTo(w * 0.3f, h * 0.15f, w * 0.7f, h * 0.85f, w, h * 0.3f)
                                lineTo(w, 0f)
                                lineTo(0f, 0f)
                                close()
                            }
                            drawPath(
                                ribbonPath,
                                Brush.horizontalGradient(
                                    listOf(Color(0x7000FF87), Color(0x807B2CBF), Color(0x6000E5FF)),
                                ),
                            )
                            val mountainPath = Path().apply {
                                moveTo(0f, h)
                                lineTo(w * 0.25f, h * 0.6f)
                                lineTo(w * 0.5f, h * 0.8f)
                                lineTo(w * 0.75f, h * 0.5f)
                                lineTo(w, h)
                                close()
                            }
                            drawPath(mountainPath, Color(0xEE03070E))
                        }

                        ScenicType.COSMIC_NEBULA -> {
                            drawRect(
                                Brush.radialGradient(
                                    listOf(Color(0xCC7B2CBF), Color(0x99D500F9), Color(0xFF050510)),
                                    center = Offset(w * 0.5f, h * 0.5f),
                                    radius = w * 0.7f,
                                ),
                            )
                            listOf(
                                Offset(w * 0.2f, h * 0.3f),
                                Offset(w * 0.8f, h * 0.25f),
                                Offset(w * 0.4f, h * 0.75f),
                                Offset(w * 0.7f, h * 0.7f),
                                Offset(w * 0.15f, h * 0.65f),
                            ).forEach { pos ->
                                drawCircle(Color.White, radius = 2.5f, center = pos)
                                drawCircle(Color(0x80D500F9), radius = 5.5f, center = pos)
                            }
                        }

                        ScenicType.SAHARA_SUNSET -> {
                            drawRect(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF4A1505), Color(0xFFE65100), Color(0xFFFFB300)),
                                ),
                            )
                            drawCircle(Color(0xAAFFF8E7), radius = h * 0.25f, center = Offset(w * 0.75f, h * 0.4f))
                            val dune1 = Path().apply {
                                moveTo(0f, h)
                                quadraticTo(w * 0.4f, h * 0.65f, w, h * 0.85f)
                                lineTo(w, h)
                                close()
                            }
                            drawPath(dune1, Color(0xDD3E1300))
                            val dune2 = Path().apply {
                                moveTo(0f, h * 0.88f)
                                quadraticTo(w * 0.6f, h * 0.75f, w, h * 0.6f)
                                lineTo(w, h)
                                lineTo(0f, h)
                                close()
                            }
                            drawPath(dune2, Color(0xFF260C00))
                        }

                        ScenicType.ALPINE_MIST -> {
                            drawRect(
                                Brush.verticalGradient(listOf(Color(0xFF0F1A2A), Color(0xFF243B55))),
                            )
                            val peak = Path().apply {
                                moveTo(0f, h)
                                lineTo(w * 0.45f, h * 0.3f)
                                lineTo(w * 0.9f, h)
                                close()
                            }
                            drawPath(peak, Color(0xDD0B121E))
                            drawRect(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color(0x70FFFFFF), Color(0x90243B55)),
                                    startY = h * 0.6f,
                                ),
                            )
                        }

                        ScenicType.TOKYO_CYBER -> {
                            drawRect(
                                Brush.verticalGradient(listOf(Color(0xFF0A0017), Color(0xFF1F0038))),
                            )
                            drawRect(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color(0x80FF007F)),
                                    startY = h * 0.5f,
                                ),
                            )
                            for (i in 1..4) {
                                drawLine(
                                    color = Color(0x6000E5FF),
                                    start = Offset(w * (i * 0.2f), h * 0.5f),
                                    end = Offset(w * (i * 0.22f), h),
                                    strokeWidth = 2f,
                                )
                            }
                        }

                        ScenicType.OBSIDIAN_SILK -> {
                            drawRect(
                                Brush.radialGradient(
                                    listOf(Color(0xFF25201A), Color(0xFF100E0B), Color(0xFF050505)),
                                ),
                            )
                            val wave = Path().apply {
                                moveTo(0f, h * 0.4f)
                                cubicTo(w * 0.3f, h * 0.8f, w * 0.7f, h * 0.2f, w, h * 0.6f)
                                lineTo(w, h)
                                lineTo(0f, h)
                                close()
                            }
                            drawPath(
                                wave,
                                Brush.linearGradient(listOf(Color(0x55FFD700), Color(0x22C49A45))),
                            )
                        }
                    }
                }

                // Active Badge Tag
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(colors.accentPrimary)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Check,
                                contentDescription = "Active",
                                tint = Color(0xFF0A0C14),
                                modifier = Modifier.size(12.dp),
                            )
                            Text(
                                text = "ACTIVE",
                                style = LunmedTheme.typography.labelMeta.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 9.sp,
                                    letterSpacing = 0.5.sp,
                                ),
                                color = Color(0xFF0A0C14),
                            )
                        }
                    }
                }
            }

            // Info Section
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = wallpaper.title,
                    style = LunmedTheme.typography.titleCard.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                    ),
                    color = colors.textPrimary,
                    maxLines = 1,
                )
                Text(
                    text = wallpaper.description,
                    style = LunmedTheme.typography.labelMeta.copy(fontSize = 11.sp),
                    color = colors.textSecondary,
                    maxLines = 2,
                )
            }
        }
    }
}
