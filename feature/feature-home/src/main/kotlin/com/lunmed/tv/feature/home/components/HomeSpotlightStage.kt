package com.lunmed.tv.feature.home.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Text
import com.lunmed.tv.core.design.theme.LunmedTheme
import com.lunmed.tv.core.model.AppInfo

/**
 * Cinematic, ultra-minimal hero spotlight stage that crossfades smoothly
 * as the user moves D-pad focus across applications.
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
            .height(104.dp),
        contentAlignment = Alignment.CenterStart,
    ) {
        AnimatedContent(
            targetState = focusedApp,
            transitionSpec = {
                (fadeIn(anim.transitionSpec()) + slideInVertically(anim.transitionSpec()) { it / 6 })
                    .togetherWith(fadeOut(anim.focusSpec()))
            },
            contentKey = { it?.packageName ?: "empty" },
            label = "HomeSpotlightCrossfade",
        ) { app ->
            if (app == null) {
                Column(verticalArrangement = Arrangement.Center) {
                    Text(
                        text = "Welcome to LUNMED TV",
                        style = LunmedTheme.typography.displayHero,
                        color = colors.textPrimary,
                    )
                    Spacer(modifier = Modifier.height(spacing.xxs))
                    Text(
                        text = "$totalAppsCount applications ready",
                        style = LunmedTheme.typography.bodySecondary,
                        color = colors.textSecondary,
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.Center) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(spacing.sm),
                    ) {
                        SpotlightBadge(
                            text = if (app.isTvApp) "ANDROID TV" else "APP",
                            highlighted = true,
                        )
                        app.categoryId?.let { cat ->
                            SpotlightBadge(
                                text = cat.uppercase(),
                                highlighted = false,
                            )
                        }
                        if (app.isFavorite) {
                            SpotlightBadge(text = "FAVORITE", highlighted = false)
                        }
                    }

                    Spacer(modifier = Modifier.height(spacing.xs))

                    Text(
                        text = app.label,
                        style = LunmedTheme.typography.displayHero,
                        color = colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )

                    Spacer(modifier = Modifier.height(spacing.xxs))

                    Text(
                        text = "Press OK to launch  •  Hold OK for options",
                        style = LunmedTheme.typography.bodySecondary,
                        color = colors.textMuted,
                    )
                }
            }
        }
    }
}

@Composable
private fun SpotlightBadge(
    text: String,
    highlighted: Boolean,
) {
    val colors = LunmedTheme.colors
    Box(
        modifier = Modifier
            .background(
                color = if (highlighted) {
                    colors.accentPrimary.copy(alpha = 0.16f)
                } else {
                    colors.surfaceElevated
                },
                shape = LunmedTheme.shapes.pill,
            )
            .padding(horizontal = 10.dp, vertical = 3.dp),
    ) {
        Text(
            text = text,
            style = LunmedTheme.typography.labelMeta,
            color = if (highlighted) colors.accentPrimary else colors.textSecondary,
        )
    }
}

