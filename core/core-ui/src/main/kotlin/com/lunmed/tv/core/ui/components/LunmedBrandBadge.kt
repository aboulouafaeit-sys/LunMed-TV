package com.lunmed.tv.core.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Text
import com.lunmed.tv.core.design.theme.LunmedTheme

enum class LunmedBrandBadgeSize {
    COMPACT,
    MEDIUM,
    HERO,
}

/**
 * World-class luxury brand emblem for LUNMED TV.
 * Features an architectural champagne-gold & obsidian monogram inside a cinematic display frame.
 */
@Composable
fun LunmedBrandBadge(
    modifier: Modifier = Modifier,
    size: LunmedBrandBadgeSize = LunmedBrandBadgeSize.COMPACT,
    showWordmark: Boolean = true,
    animatedSheen: Boolean = false,
) {
    val colors = LunmedTheme.colors

    val sheenProgress = if (animatedSheen) {
        val transition = rememberInfiniteTransition(label = "SheenTransition")
        val progress by transition.animateFloat(
            initialValue = -1.2f,
            targetValue = 2.2f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 3200, easing = LinearEasing),
                repeatMode = RepeatMode.Restart,
            ),
            label = "SheenAnim",
        )
        progress
    } else {
        0f
    }

    when (size) {
        LunmedBrandBadgeSize.COMPACT -> {
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                LunmedMonogramEmblem(
                    size = 28.dp,
                    accentColor = colors.accentPrimary,
                    sheenProgress = sheenProgress,
                )
                if (showWordmark) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = "LUNMED",
                            style = LunmedTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.2.sp,
                            ),
                            color = colors.textPrimary,
                        )
                        Box(
                            modifier = Modifier
                                .background(
                                    color = colors.surfaceCard,
                                    shape = RoundedCornerShape(4.dp),
                                )
                                .border(
                                    width = 1.dp,
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            colors.accentPrimary,
                                            colors.accentSecondary,
                                        ),
                                    ),
                                    shape = RoundedCornerShape(4.dp),
                                )
                                .padding(horizontal = 4.dp, vertical = 1.5.dp),
                        ) {
                            Text(
                                text = "TV",
                                style = LunmedTheme.typography.labelMeta.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 1.2.sp,
                                ),
                                color = colors.accentPrimary,
                            )
                        }
                    }
                }
            }
        }

        LunmedBrandBadgeSize.MEDIUM -> {
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                LunmedMonogramEmblem(
                    size = 46.dp,
                    accentColor = colors.accentPrimary,
                    sheenProgress = sheenProgress,
                )
                if (showWordmark) {
                    Column(verticalArrangement = Arrangement.Center) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                text = "LUNMED",
                                style = LunmedTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 3.sp,
                                ),
                                color = colors.textPrimary,
                            )
                            Box(
                                modifier = Modifier
                                    .background(
                                        color = colors.surfaceElevated,
                                        shape = RoundedCornerShape(4.dp),
                                    )
                                    .border(
                                        width = 1.2.dp,
                                        brush = Brush.linearGradient(
                                            listOf(colors.accentPrimary, colors.accentSecondary),
                                        ),
                                        shape = RoundedCornerShape(4.dp),
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                            ) {
                                Text(
                                    text = "TV",
                                    style = LunmedTheme.typography.labelMeta.copy(
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.5.sp,
                                    ),
                                    color = colors.accentPrimary,
                                )
                            }
                        }
                        Text(
                            text = "CINEMATIC LAUNCHER",
                            style = LunmedTheme.typography.labelMeta.copy(
                                letterSpacing = 2.sp,
                                fontSize = 9.sp,
                            ),
                            color = colors.textMuted,
                        )
                    }
                }
            }
        }

        LunmedBrandBadgeSize.HERO -> {
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                LunmedMonogramEmblem(
                    size = 110.dp,
                    accentColor = colors.accentPrimary,
                    sheenProgress = sheenProgress,
                )
                if (showWordmark) {
                    Spacer(modifier = Modifier.height(18.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Text(
                            text = "LUNMED",
                            style = LunmedTheme.typography.displayHero.copy(
                                letterSpacing = 6.sp,
                                fontWeight = FontWeight.Bold,
                            ),
                            color = colors.textPrimary,
                        )
                        Box(
                            modifier = Modifier
                                .background(
                                    color = colors.surfaceElevated,
                                    shape = RoundedCornerShape(6.dp),
                                )
                                .border(
                                    width = 1.5.dp,
                                    brush = Brush.linearGradient(
                                        listOf(colors.accentPrimary, colors.accentSecondary),
                                    ),
                                    shape = RoundedCornerShape(6.dp),
                                )
                                .padding(horizontal = 8.dp, vertical = 3.dp),
                        ) {
                            Text(
                                text = "TV",
                                style = LunmedTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 2.sp,
                                ),
                                color = colors.accentPrimary,
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "HIGH-END CINEMATIC ANDROID TV",
                        style = LunmedTheme.typography.labelAction.copy(
                            letterSpacing = 3.5.sp,
                            fontSize = 11.sp,
                        ),
                        color = colors.accentPrimary.copy(alpha = 0.85f),
                    )
                }
            }
        }
    }
}

/**
 * Geometric, GPU-accelerated canvas emblem rendering the architectural 'L' monogram
 * with champagne-gold beveled strokes and glossy obsidian insets.
 */
@Composable
fun LunmedMonogramEmblem(
    size: Dp,
    accentColor: Color,
    modifier: Modifier = Modifier,
    sheenProgress: Float = 0f,
) {
    val goldLight = Color(0xFFFFF8E7)
    val goldMid = accentColor
    val goldDark = Color(0xFF8A651E)
    val obsidianBack = Color(0xFF07090E)
    val obsidianGlass = Color(0xFF0D121C)

    Canvas(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.18f)),
    ) {
        val w = this.size.width
        val h = this.size.height

        // 1. Obsidian Backing
        drawRoundRect(
            color = obsidianBack,
            cornerRadius = CornerRadius(w * 0.18f, h * 0.18f),
        )

        // 2. Ambient Amber/Gold Radial Glow in center
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    goldMid.copy(alpha = 0.45f),
                    goldMid.copy(alpha = 0.12f),
                    Color.Transparent,
                ),
                center = Offset(w * 0.5f, h * 0.5f),
                radius = w * 0.48f,
            ),
        )

        // 3. Cinema Screen Outer Frame
        val frameRect = RoundRect(
            rect = Rect(
                left = w * 0.08f,
                top = h * 0.14f,
                right = w * 0.92f,
                bottom = h * 0.86f,
            ),
            cornerRadius = CornerRadius(w * 0.10f, h * 0.10f),
        )
        val framePath = Path().apply { addRoundRect(frameRect) }

        // Fill inner screen with glossy obsidian glass
        drawPath(
            path = framePath,
            color = obsidianGlass,
            style = Fill,
        )

        // Stroke cinema frame with metallic gold gradient
        drawPath(
            path = framePath,
            brush = Brush.linearGradient(
                colors = listOf(goldLight, goldMid, goldDark, goldMid),
                start = Offset(0f, 0f),
                end = Offset(w, h),
            ),
            style = Stroke(
                width = w * 0.055f,
                join = StrokeJoin.Round,
                cap = StrokeCap.Round,
            ),
        )

        // 4. Monumental "L" Crest with architectural angled bevel
        val stemLeft = w * 0.32f
        val stemRight = w * 0.48f
        val topPeak = h * 0.22f
        val baseY = h * 0.74f
        val footRight = w * 0.76f
        val footTop = h * 0.60f

        val lShadowPath = Path().apply {
            moveTo(stemLeft + w * 0.02f, topPeak + h * 0.02f)
            lineTo(stemRight + w * 0.02f, topPeak - h * 0.04f)
            lineTo(stemRight + w * 0.02f, footTop + h * 0.02f)
            lineTo(footRight + w * 0.02f, footTop + h * 0.02f)
            lineTo(footRight - w * 0.04f, baseY + h * 0.02f)
            lineTo(stemLeft + w * 0.02f, baseY + h * 0.02f)
            close()
        }
        drawPath(path = lShadowPath, color = Color(0x66000000))

        val lMainPath = Path().apply {
            moveTo(stemLeft, topPeak)
            lineTo(stemRight, topPeak - h * 0.06f)
            lineTo(stemRight, footTop)
            lineTo(footRight, footTop)
            lineTo(footRight - w * 0.06f, baseY)
            lineTo(stemLeft, baseY)
            close()
        }

        // Fill Monogram L with metallic gradient
        drawPath(
            path = lMainPath,
            brush = Brush.linearGradient(
                colors = listOf(goldLight, goldMid, goldDark),
                start = Offset(stemLeft, topPeak),
                end = Offset(footRight, baseY),
            ),
            style = Fill,
        )

        // Inner cut of L (obsidian glass core)
        val lInnerPath = Path().apply {
            val inset = w * 0.04f
            moveTo(stemLeft + inset, topPeak + inset * 1.5f)
            lineTo(stemRight - inset, topPeak - h * 0.03f)
            lineTo(stemRight - inset, footTop - inset)
            lineTo(footRight - inset * 2.2f, footTop - inset)
            lineTo(footRight - inset * 3.2f, baseY - inset)
            lineTo(stemLeft + inset, baseY - inset)
            close()
        }
        drawPath(path = lInnerPath, color = obsidianBack, style = Fill)

        // Specular highlight stroke on Monogram L
        drawPath(
            path = lMainPath,
            brush = Brush.linearGradient(
                colors = listOf(goldLight, goldMid),
                start = Offset(stemLeft, topPeak),
                end = Offset(stemRight, footTop),
            ),
            style = Stroke(
                width = w * 0.025f,
                join = StrokeJoin.Miter,
            ),
        )

        // 5. Dynamic Sheen Sweep (if animated)
        if (sheenProgress != 0f) {
            val sheenX = w * sheenProgress
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = 0.28f),
                        Color.Transparent,
                    ),
                    start = Offset(sheenX - w * 0.3f, 0f),
                    end = Offset(sheenX + w * 0.3f, h),
                ),
                cornerRadius = CornerRadius(w * 0.18f, h * 0.18f),
            )
        }
    }
}
