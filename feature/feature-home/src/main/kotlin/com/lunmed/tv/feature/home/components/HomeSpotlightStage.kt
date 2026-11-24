package com.lunmed.tv.feature.home.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import com.lunmed.tv.core.design.theme.LunmedTheme
import com.lunmed.tv.core.model.AppInfo
import com.lunmed.tv.core.ui.components.AdaptiveAuraEngine

/**
 * World-class cinematic hero spotlight stage.
 * Employs adaptive atmospheric backlighting, staggered spring transitions,
 * and illuminated 10-foot remote keycaps.
 */
@Composable
fun HomeSpotlightStage(
    focusedApp: AppInfo?,
    totalAppsCount: Int,
    modifier: Modifier = Modifier,
) {
    val colors = LunmedTheme.colors
    val spacing = LunmedTheme.spacing

    val auraColor = remember(focusedApp?.packageName, focusedApp?.categoryId) {
        AdaptiveAuraEngine.auraForApp(focusedApp, colors.accentPrimary)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.overscanHorizontal)
            .height(126.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        // Living Adaptive Stage Ambient Aura Bloom
        Box(
            modifier = Modifier
                .size(width = 540.dp, height = 120.dp)
                .alpha(0.38f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            auraColor.copy(alpha = 0.55f),
                            auraColor.copy(alpha = 0.15f),
                            Color.Transparent,
                        ),
                    ),
                    shape = CircleShape,
                ),
        )

        AnimatedContent(
            targetState = focusedApp,
            transitionSpec = {
                (fadeIn(tween(durationMillis = 320, easing = FastOutSlowInEasing)) +
                    slideInHorizontally(tween(durationMillis = 340, easing = FastOutSlowInEasing)) { it / 8 })
                    .togetherWith(fadeOut(tween(durationMillis = 180, easing = FastOutSlowInEasing)))
            },
            contentKey = { it?.packageName ?: "empty_stage" },
            label = "HomeSpotlightStageAnimatedContent",
        ) { app ->
            if (app == null) {
                Column(verticalArrangement = Arrangement.Center) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(colors.accentPrimary, CircleShape),
                        )
                        Text(
                            text = "LUNMED SIGNATURE THEATER",
                            style = LunmedTheme.typography.labelAction.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp,
                            ),
                            color = colors.accentPrimary,
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Cinema Entertainment Experience",
                        style = LunmedTheme.typography.displayHero.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                        color = colors.textPrimary,
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "$totalAppsCount applications optimized for 4K TV remote navigation",
                        style = LunmedTheme.typography.bodySecondary,
                        color = colors.textSecondary,
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.Center) {
                    // Top Metadata Badge Cluster
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        SpotlightBadge(
                            text = if (app.isTvApp) "ANDROID TV" else "APP",
                            icon = if (app.isTvApp) Icons.Rounded.Tv else null,
                            accentColor = auraColor,
                            highlighted = true,
                        )

                        app.categoryId?.let { cat ->
                            SpotlightBadge(
                                text = cat.uppercase(),
                                accentColor = auraColor,
                                highlighted = false,
                                showDot = true,
                            )
                        }

                        if (app.isFavorite) {
                            SpotlightBadge(
                                text = "FAVORITE",
                                icon = Icons.Rounded.Star,
                                accentColor = colors.accentPrimary,
                                highlighted = false,
                                isGold = true,
                            )
                        }

                        if (app.launchCount > 0) {
                            SpotlightBadge(
                                text = "PLAYED ${app.launchCount}×",
                                icon = Icons.Rounded.PlayArrow,
                                accentColor = colors.textSecondary,
                                highlighted = false,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(spacing.xs))

                    // Hero Application Title with subtle gradient
                    Text(
                        text = app.label,
                        style = LunmedTheme.typography.displayHero.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp,
                        ),
                        color = colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Illuminated Remote Keycap Shortcuts
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        RemoteKeycapHint(key = "OK", action = "Launch App", accentColor = auraColor)
                        Text(
                            text = "•",
                            style = LunmedTheme.typography.bodySecondary,
                            color = colors.textMuted,
                        )
                        RemoteKeycapHint(key = "HOLD OK", action = "Options & Settings", accentColor = colors.accentPrimary)
                    }
                }
            }
        }
    }
}

@Composable
private fun RemoteKeycapHint(
    key: String,
    action: String,
    accentColor: Color,
) {
    val colors = LunmedTheme.colors
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = colors.surfaceElevated.copy(alpha = 0.9f),
                    shape = RoundedCornerShape(5.dp),
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            accentColor.copy(alpha = 0.7f),
                            colors.borderSubtle,
                        ),
                    ),
                    shape = RoundedCornerShape(5.dp),
                )
                .padding(horizontal = 7.dp, vertical = 2.dp),
        ) {
            Text(
                text = key,
                style = LunmedTheme.typography.labelMeta.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp,
                ),
                color = accentColor,
            )
        }
        Text(
            text = action,
            style = LunmedTheme.typography.bodySecondary.copy(
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
            ),
            color = colors.textSecondary,
        )
    }
}

@Composable
private fun SpotlightBadge(
    text: String,
    icon: ImageVector? = null,
    accentColor: Color,
    highlighted: Boolean = false,
    isGold: Boolean = false,
    showDot: Boolean = false,
) {
    val colors = LunmedTheme.colors
    val bg = when {
        highlighted -> accentColor.copy(alpha = 0.18f)
        isGold -> colors.accentPrimary.copy(alpha = 0.14f)
        else -> colors.surfaceElevated.copy(alpha = 0.85f)
    }
    val borderCol = when {
        highlighted -> accentColor.copy(alpha = 0.5f)
        isGold -> colors.accentPrimary.copy(alpha = 0.45f)
        else -> colors.borderSubtle
    }
    val contentCol = when {
        highlighted -> accentColor
        isGold -> colors.accentPrimary
        else -> colors.textSecondary
    }

    Box(
        modifier = Modifier
            .background(color = bg, shape = LunmedTheme.shapes.pill)
            .border(width = 0.9.dp, color = borderCol, shape = LunmedTheme.shapes.pill)
            .padding(horizontal = 9.dp, vertical = 3.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            if (showDot) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .background(accentColor, CircleShape),
                )
            }
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentCol,
                    modifier = Modifier.size(12.dp),
                )
            }
            Text(
                text = text,
                style = LunmedTheme.typography.labelMeta.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    fontSize = 11.sp,
                ),
                color = contentCol,
            )
        }
    }
}
