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
import com.lunmed.tv.core.model.IconDisplayMode
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
    iconDisplayMode: IconDisplayMode = IconDisplayMode.CINEMA_BANNER,
) {
    val context = LocalContext.current
    val colors = LunmedTheme.colors
    val spacing = LunmedTheme.spacing
    var isCardFocused by remember { mutableStateOf(false) }

    val (cardWidth, cardHeight) = remember(cardSize, iconDisplayMode) {
        when (iconDisplayMode) {
            IconDisplayMode.SQUARE_IOS -> when (cardSize) {
                CardSizePreset.COMPACT -> 118.dp to 118.dp
                CardSizePreset.STANDARD -> 142.dp to 142.dp
                CardSizePreset.CINEMA -> 168.dp to 168.dp
            }
            IconDisplayMode.COMPACT_CAPSULE -> when (cardSize) {
                CardSizePreset.COMPACT -> 176.dp to 60.dp
                CardSizePreset.STANDARD -> 208.dp to 64.dp
                CardSizePreset.CINEMA -> 240.dp to 70.dp
            }
            IconDisplayMode.CINEMA_BANNER -> when (cardSize) {
                CardSizePreset.COMPACT -> 168.dp to 96.dp
                CardSizePreset.STANDARD -> 200.dp to 114.dp
                CardSizePreset.CINEMA -> 240.dp to 136.dp
            }
        }
    }

    val cardShape = remember(iconDisplayMode) {
        when (iconDisplayMode) {
            IconDisplayMode.SQUARE_IOS -> RoundedCornerShape(26.dp)
            IconDisplayMode.COMPACT_CAPSULE -> RoundedCornerShape(16.dp)
            IconDisplayMode.CINEMA_BANNER -> RoundedCornerShape(18.dp)
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

    val innerArtworkScale by animateFloatAsState(
        targetValue = if (isCardFocused) 1.05f else 1.0f,
        animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing),
        label = "HomeCardInnerZoom",
    )

    val labelColor by animateColorAsState(
        targetValue = if (isCardFocused) colors.textPrimary else colors.textSecondary,
        label = "HomeAppCardLabelColor",
    )

    if (iconDisplayMode == IconDisplayMode.COMPACT_CAPSULE) {
        LunmedFocusableCard(
            onClick = onClick,
            onLongClick = onLongClick,
            shape = cardShape,
            modifier = modifier
                .width(cardWidth)
                .height(cardHeight)
                .onFocusChanged {
                    isCardFocused = it.isFocused
                    if (it.isFocused) onFocused()
                },
        ) { isFocused ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                if (isFocused) colors.surfaceCardFocused else colors.surfaceElevated,
                                colors.surfaceCard,
                            ),
                        ),
                    )
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                val loaded = artwork
                if (loaded != null) {
                    Image(
                        bitmap = loaded.bitmap,
                        contentDescription = app.label,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp)),
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(colors.surfaceElevated, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = app.label.take(1).uppercase(),
                            style = LunmedTheme.typography.titleCard,
                            color = colors.accentPrimary,
                        )
                    }
                }

                Text(
                    text = app.label,
                    style = LunmedTheme.typography.bodyPrimary.copy(
                        fontWeight = if (isFocused) FontWeight.Bold else FontWeight.Medium,
                    ),
                    color = if (isFocused) colors.accentPrimary else colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )

                if (app.isFavorite) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = "Favorite",
                        tint = colors.accentPrimary,
                        modifier = Modifier.size(14.dp),
                    )
                }
            }
        }
    } else {
        Column(
            modifier = modifier.width(cardWidth),
            horizontalAlignment = Alignment.Start,
        ) {
            LunmedFocusableCard(
                onClick = onClick,
                onLongClick = onLongClick,
                shape = cardShape,
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
                        if (loaded.isBanner && iconDisplayMode == IconDisplayMode.CINEMA_BANNER) {
                            Image(
                                bitmap = loaded.bitmap,
                                contentDescription = app.label,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .scale(innerArtworkScale),
                            )
                        } else {
                            val iconSize = if (iconDisplayMode == IconDisplayMode.SQUARE_IOS) 64.dp else 56.dp
                            Box(
                                modifier = Modifier
                                    .size(iconSize)
                                    .scale(innerArtworkScale)
                                    .background(
                                        brush = Brush.verticalGradient(
                                            listOf(
                                                if (isFocused) appAura.copy(alpha = 0.25f) else colors.surfaceElevated.copy(alpha = 0.9f),
                                                colors.surfaceDeep,
                                            ),
                                        ),
                                        shape = RoundedCornerShape(16.dp),
                                    )
                                    .border(
                                        width = 1.2.dp,
                                        color = if (isFocused) appAura.copy(alpha = 0.7f) else colors.borderGlass,
                                        shape = RoundedCornerShape(16.dp),
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Image(
                                    bitmap = loaded.bitmap,
                                    contentDescription = app.label,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .size(iconSize - 14.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                )
                            }
                        }
                    } else {
                        val iconSize = if (iconDisplayMode == IconDisplayMode.SQUARE_IOS) 64.dp else 56.dp
                        Box(
                            modifier = Modifier
                                .size(iconSize)
                                .scale(innerArtworkScale)
                                .background(colors.surfaceElevated, RoundedCornerShape(16.dp))
                                .border(1.dp, colors.borderGlass, RoundedCornerShape(16.dp)),
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
}
