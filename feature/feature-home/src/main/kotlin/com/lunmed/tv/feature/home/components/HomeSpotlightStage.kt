package com.lunmed.tv.feature.home.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
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
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.runtime.Composable
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

/**
 * Cinematic, ultra-luxury hero spotlight stage that crossfades smoothly
 * as the user navigates across applications with the remote control.
 */
@Composable
fun HomeSpotlightStage(
    focusedApp: AppInfo?,
    totalAppsCount: Int,
    modifier: Modifier = Modifier,
) {
    val colors = LunmedTheme.colors
    val spacing = LunmedTheme.spacing
    val anim = LunmedTheme.animation

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = spacing.overscanHorizontal)
            .height(118.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        // Ambient Radial Aura behind Hero content
        Box(
            modifier = Modifier
                .size(width = 460.dp, height = 110.dp)
                .alpha(0.35f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            colors.accentGlow,
                            Color.Transparent,
                        ),
                    ),
                    shape = CircleShape,
                ),
        )

        AnimatedContent(
            targetState = focusedApp,
            transitionSpec = {
                (fadeIn(anim.transitionSpec()) + slideInVertically(anim.transitionSpec()) { it / 4 })
                    .togetherWith(fadeOut(anim.focusSpec()))
            },
            contentKey = { it?.packageName ?: "empty" },
            label = "HomeSpotlightCrossfade",
        ) { app ->
            if (app == null) {
                Column(verticalArrangement = Arrangement.Center) {
                    Text(
                        text = "LUNMED ENTERTAINMENT",
                        style = LunmedTheme.typography.displayHero.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.sp,
                        ),
                        color = colors.textPrimary,
                    )
                    Spacer(modifier = Modifier.height(spacing.xxs))
                    Text(
                        text = "$totalAppsCount applications optimized for 4K TV navigation",
                        style = LunmedTheme.typography.bodySecondary,
                        color = colors.textSecondary,
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.Center) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        SpotlightBadge(
                            text = if (app.isTvApp) "ANDROID TV" else "APP",
                            icon = if (app.isTvApp) Icons.Rounded.Tv else null,
                            highlighted = true,
                        )
                        app.categoryId?.let { cat ->
                            SpotlightBadge(
                                text = cat.uppercase(),
                                highlighted = false,
                            )
                        }
                        if (app.isFavorite) {
                            SpotlightBadge(
                                text = "FAVORITE",
                                icon = Icons.Rounded.Star,
                                highlighted = false,
                                isGold = true,
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(spacing.xs))

                    Text(
                        text = app.label,
                        style = LunmedTheme.typography.displayHero.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp,
                        ),
                        color = colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Spacer(modifier = Modifier.height(spacing.xs))

                    // Remote Keycap Guidance Bar
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        RemoteKeycapHint(key = "OK", action = "Launch")
                        Text(
                            text = "•",
                            style = LunmedTheme.typography.bodySecondary,
                            color = colors.textMuted,
                        )
                        RemoteKeycapHint(key = "HOLD OK", action = "Menu")
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
) {
    val colors = LunmedTheme.colors
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = colors.surfaceElevated.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(4.dp),
                )
                .border(
                    width = 0.8.dp,
                    color = colors.borderSubtle,
                    shape = RoundedCornerShape(4.dp),
                )
                .padding(horizontal = 6.dp, vertical = 2.dp),
        ) {
            Text(
                text = key,
                style = LunmedTheme.typography.labelMeta.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                ),
                color = colors.accentPrimary,
            )
        }
        Text(
            text = action,
            style = LunmedTheme.typography.bodySecondary.copy(
                fontSize = 12.sp,
            ),
            color = colors.textSecondary,
        )
    }
}

@Composable
private fun SpotlightBadge(
    text: String,
    icon: ImageVector? = null,
    highlighted: Boolean = false,
    isGold: Boolean = false,
) {
    val colors = LunmedTheme.colors
    val bg = when {
        highlighted -> colors.accentPrimary.copy(alpha = 0.16f)
        isGold -> colors.accentPrimary.copy(alpha = 0.10f)
        else -> colors.surfaceElevated.copy(alpha = 0.8f)
    }
    val borderCol = when {
        highlighted -> colors.accentPrimary.copy(alpha = 0.45f)
        isGold -> colors.accentPrimary.copy(alpha = 0.35f)
        else -> colors.borderSubtle
    }
    val contentCol = when {
        highlighted || isGold -> colors.accentPrimary
        else -> colors.textSecondary
    }

    Box(
        modifier = Modifier
            .background(color = bg, shape = LunmedTheme.shapes.pill)
            .border(width = 0.8.dp, color = borderCol, shape = LunmedTheme.shapes.pill)
            .padding(horizontal = 10.dp, vertical = 3.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
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
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.8.sp,
                ),
                color = contentCol,
            )
        }
    }
}
