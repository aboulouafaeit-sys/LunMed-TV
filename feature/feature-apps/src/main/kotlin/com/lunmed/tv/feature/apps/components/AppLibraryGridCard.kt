package com.lunmed.tv.feature.apps.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material.icons.rounded.VisibilityOff
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
import androidx.compose.ui.text.style.TextAlign
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
import com.lunmed.tv.feature.apps.model.AppSortOrder

/**
 * World-class polymorphic App Library Grid Card.
 * Fully synchronizes with the user's chosen IconDisplayMode:
 * 1. CINEMA_BANNER: 16:9 cinematic widescreen preview card.
 * 2. SQUARE_IOS: Authentic Apple tvOS rounded squircle with 3D glass pedestal.
 * 3. COMPACT_CAPSULE: Modern horizontal capsule dock with app icon & inline typography.
 */
@Composable
fun AppLibraryGridCard(
    app: AppInfo,
    cardSize: CardSizePreset,
    iconDisplayMode: IconDisplayMode = IconDisplayMode.CINEMA_BANNER,
    sortOrder: AppSortOrder,
    onFocused: () -> Unit,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val colors = LunmedTheme.colors
    val spacing = LunmedTheme.spacing
    var isCardFocused by remember { mutableStateOf(false) }

    val (cardWidth, cardHeight) = remember(cardSize, iconDisplayMode) {
        when (iconDisplayMode) {
            IconDisplayMode.SQUARE_IOS -> when (cardSize) {
                CardSizePreset.COMPACT -> 118.dp to 118.dp
                CardSizePreset.STANDARD -> 140.dp to 140.dp
                CardSizePreset.CINEMA -> 164.dp to 164.dp
            }
            IconDisplayMode.COMPACT_CAPSULE -> when (cardSize) {
                CardSizePreset.COMPACT -> 180.dp to 58.dp
                CardSizePreset.STANDARD -> 210.dp to 64.dp
                CardSizePreset.CINEMA -> 240.dp to 70.dp
            }
            IconDisplayMode.CINEMA_BANNER -> when (cardSize) {
                CardSizePreset.COMPACT -> 160.dp to 90.dp
                CardSizePreset.STANDARD -> 192.dp to 108.dp
                CardSizePreset.CINEMA -> 224.dp to 126.dp
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
        label = "GridCardInnerZoom",
    )

    val labelColor by animateColorAsState(
        targetValue = if (isCardFocused) colors.textPrimary else colors.textSecondary,
        label = "AppLibraryGridLabelColor",
    )

    val metaText = when (sortOrder) {
        AppSortOrder.MOST_USED -> if (app.launchCount > 0) "Opened ${app.launchCount}×" else "Never opened"
        AppSortOrder.RECENTLY_USED -> if (app.lastLaunchedEpochMs > 0) "Recently opened" else "Never opened"
        else -> app.categoryId?.uppercase() ?: app.packageName
    }

    if (iconDisplayMode == IconDisplayMode.COMPACT_CAPSULE) {
        // --- 1. COMPACT CAPSULE DOCK MODE ---
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
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
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

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = app.label,
                        style = LunmedTheme.typography.bodyPrimary.copy(
                            fontWeight = if (isFocused) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp,
                        ),
                        color = if (isFocused) colors.accentPrimary else colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = metaText,
                        style = LunmedTheme.typography.labelMeta.copy(fontSize = 10.sp),
                        color = colors.textMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                if (app.isFavorite) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = "Favorite",
                        tint = colors.accentPrimary,
                        modifier = Modifier.size(13.dp),
                    )
                }
            }
        }
    } else if (iconDisplayMode == IconDisplayMode.SQUARE_IOS) {
        // --- 2. APPLE TV SQUIRCLE MODE ---
        Column(
            modifier = modifier.width(cardWidth),
            horizontalAlignment = Alignment.CenterHorizontally,
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
                                listOf(
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
                                        listOf(appAura.copy(alpha = 0.45f), Color.Transparent),
                                    ),
                                ),
                        )
                    }

                    val loaded = artwork
                    if (loaded != null) {
                        Image(
                            bitmap = loaded.bitmap,
                            contentDescription = app.label,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(62.dp)
                                .scale(innerArtworkScale)
                                .clip(RoundedCornerShape(14.dp)),
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .background(colors.surfaceElevated, RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = app.label.take(2).uppercase(),
                                style = LunmedTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                ),
                                color = if (isFocused) appAura else colors.textMuted,
                            )
                        }
                    }

                    // Top badges
                    if (app.isFavorite) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(6.dp)
                                .background(colors.obsidianCanvas.copy(alpha = 0.85f), CircleShape)
                                .padding(3.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Star,
                                contentDescription = "Favorite",
                                tint = colors.accentPrimary,
                                modifier = Modifier.size(11.dp),
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing.xs))

            Text(
                text = app.label,
                style = LunmedTheme.typography.titleCard.copy(
                    fontWeight = if (isCardFocused) FontWeight.ExtraBold else FontWeight.Medium,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                ),
                color = labelColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = metaText,
                style = LunmedTheme.typography.labelMeta.copy(
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                ),
                color = colors.textMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    } else {
        // --- 3. CINEMA BANNER (16:9) MODE ---
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
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .scale(innerArtworkScale)
                                    .background(
                                        brush = Brush.verticalGradient(
                                            listOf(
                                                if (isFocused) appAura.copy(alpha = 0.25f) else colors.surfaceElevated.copy(alpha = 0.9f),
                                                colors.surfaceDeep,
                                            ),
                                        ),
                                        shape = RoundedCornerShape(13.dp),
                                    )
                                    .border(
                                        width = 1.2.dp,
                                        color = if (isFocused) appAura.copy(alpha = 0.7f) else Color(0x18FFFFFF),
                                        shape = RoundedCornerShape(13.dp),
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Image(
                                    bitmap = loaded.bitmap,
                                    contentDescription = app.label,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(9.dp)),
                                )
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .scale(innerArtworkScale)
                                .background(colors.surfaceElevated, RoundedCornerShape(13.dp))
                                .border(1.dp, Color(0x18FFFFFF), RoundedCornerShape(13.dp)),
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

                    // Top right badges
                    Row(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        if (app.isHidden) {
                            Box(
                                modifier = Modifier
                                    .background(colors.statusWarning.copy(alpha = 0.85f), LunmedTheme.shapes.pill)
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.VisibilityOff,
                                    contentDescription = "Hidden",
                                    tint = colors.obsidianCanvas,
                                    modifier = Modifier.size(10.dp),
                                )
                            }
                        }

                        if (app.isFavorite) {
                            Box(
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .background(
                                        color = colors.obsidianCanvas.copy(alpha = 0.88f),
                                        shape = CircleShape,
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isFocused) colors.accentPrimary else colors.accentPrimary.copy(alpha = 0.5f),
                                        shape = CircleShape,
                                    )
                                    .padding(3.dp),
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Star,
                                    contentDescription = "Favorite",
                                    tint = colors.accentPrimary,
                                    modifier = Modifier.size(11.dp),
                                )
                            }
                        }
                    }

                    // Category pill on bottom-left of card
                    app.categoryId?.let { cat ->
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(6.dp)
                                .background(
                                    color = colors.obsidianCanvas.copy(alpha = 0.82f),
                                    shape = LunmedTheme.shapes.pill,
                                )
                                .border(
                                    width = 0.8.dp,
                                    color = if (isFocused) appAura.copy(alpha = 0.6f) else colors.borderSubtle,
                                    shape = LunmedTheme.shapes.pill,
                                )
                                .padding(horizontal = 7.dp, vertical = 2.dp),
                        ) {
                            Text(
                                text = cat.uppercase(),
                                style = LunmedTheme.typography.labelMeta.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp,
                                ),
                                color = if (isFocused) appAura else colors.accentPrimary.copy(alpha = 0.9f),
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing.xs))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(horizontal = 2.dp),
            ) {
                if (isCardFocused) {
                    Box(
                        modifier = Modifier
                            .size(4.dp)
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

            Text(
                text = metaText,
                style = LunmedTheme.typography.labelMeta,
                color = colors.textMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(horizontal = 2.dp),
            )
        }
    }
}
