package com.lunmed.tv.feature.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import com.lunmed.tv.core.design.theme.LunmedTheme
import com.lunmed.tv.core.model.AppInfo
import com.lunmed.tv.core.model.CardSizePreset
import com.lunmed.tv.core.ui.artwork.AppArtwork
import com.lunmed.tv.core.ui.artwork.AppArtworkCache
import com.lunmed.tv.core.ui.components.AdaptiveAuraEngine
import com.lunmed.tv.core.ui.components.LunmedFocusableCard

@Composable
fun HomeAppCard(
    app: AppInfo,
    cardSize: CardSizePreset,
    onFocused: () -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val colors = LunmedTheme.colors
    val spacing = LunmedTheme.spacing
    var isCardFocused by remember { mutableStateOf(false) }

    val (cardWidth, cardHeight) = remember(cardSize) {
        when (cardSize) {
            CardSizePreset.COMPACT -> 168.dp to 96.dp
            CardSizePreset.STANDARD -> 200.dp to 114.dp
            CardSizePreset.CINEMA -> 240.dp to 136.dp
        }
    }

    val artwork by produceState<AppArtwork?>(
        initialValue = null,
        key1 = app.packageName,
        key2 = app.activityName,
    ) {
        value = AppArtworkCache.load(context, app.packageName, app.activityName)
    }

    val appAura = remember(app.packageName, app.categoryId) {
        AdaptiveAuraEngine.auraForApp(app, colors.accentPrimary)
    }

    // Inner parallax scale: artwork expands slightly inside card on focus (Apple TV effect)
    val innerArtworkScale by animateFloatAsState(
        targetValue = if (isCardFocused) 1.05f else 1.0f,
        animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing),
        label = "HomeCardInnerZoom",
    )

    val labelColor by animateColorAsState(
        targetValue = if (isCardFocused) colors.textPrimary else colors.textSecondary,
        label = "HomeAppCardLabelColor",
    )

    Column(
        modifier = modifier.width(cardWidth),
        horizontalAlignment = Alignment.Start,
    ) {
        LunmedFocusableCard(
            onClick = onClick,
            onLongClick = onLongClick,
            modifier = Modifier
                .width(cardWidth)
                .height(cardHeight)
                .onFocusChanged {
                    isCardFocused = it.isFocused
                    if (it.isFocused) onFocused()
                },
        ) { isFocused ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                if (isFocused) colors.surfaceCardFocused else colors.surfaceElevated,
                                colors.surfaceCard,
                            ),
                        ),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                // Adaptive Ambient Aura Bloom on Focus
                if (isFocused) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        appAura.copy(alpha = 0.40f),
                                        Color.Transparent,
                                    ),
                                ),
                            ),
                    )
                }

                val loaded = artwork
                if (loaded != null) {
                    if (loaded.isBanner) {
                        Image(
                            bitmap = loaded.bitmap,
                            contentDescription = app.label,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .scale(innerArtworkScale),
                        )
                    } else {
                        // High-end multi-layer 3D glass pedestal for standard icons
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .scale(innerArtworkScale)
                                .background(
                                    brush = Brush.verticalGradient(
                                        listOf(
                                            if (isFocused) appAura.copy(alpha = 0.25f) else colors.surfaceElevated.copy(alpha = 0.9f),
                                            colors.surfaceDeep,
                                        ),
                                    ),
                                    shape = RoundedCornerShape(14.dp),
                                )
                                .border(
                                    width = 1.2.dp,
                                    color = if (isFocused) appAura.copy(alpha = 0.7f) else Color(0x18FFFFFF),
                                    shape = RoundedCornerShape(14.dp),
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Image(
                                bitmap = loaded.bitmap,
                                contentDescription = app.label,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(10.dp)),
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .scale(innerArtworkScale)
                            .background(colors.surfaceElevated, RoundedCornerShape(14.dp))
                            .border(1.dp, Color(0x18FFFFFF), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = app.label.take(2).uppercase(),
                            style = LunmedTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                            ),
                            color = if (isFocused) appAura else colors.textMuted,
                        )
                    }
                }

                // Luxury Favorite Star Emblem
                if (app.isFavorite) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(
                                color = colors.obsidianCanvas.copy(alpha = 0.88f),
                                shape = CircleShape,
                            )
                            .border(
                                width = 1.dp,
                                color = if (isFocused) colors.accentPrimary else colors.accentPrimary.copy(alpha = 0.45f),
                                shape = CircleShape,
                            )
                            .padding(4.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Star,
                            contentDescription = "Favorite",
                            tint = colors.accentPrimary,
                            modifier = Modifier.size(12.dp),
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(spacing.xs))

        // High-contrast label with animated indicator dot
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            modifier = Modifier.padding(horizontal = 4.dp),
        ) {
            if (isCardFocused) {
                Box(
                    modifier = Modifier
                        .size(5.dp)
                        .background(appAura, CircleShape),
                )
            }
            Text(
                text = app.label,
                style = LunmedTheme.typography.titleCard.copy(
                    fontWeight = if (isCardFocused) FontWeight.ExtraBold else FontWeight.Medium,
                ),
                color = labelColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
