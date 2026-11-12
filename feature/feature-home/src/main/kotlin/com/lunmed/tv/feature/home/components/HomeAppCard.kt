package com.lunmed.tv.feature.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import com.lunmed.tv.core.design.theme.LunmedTheme
import com.lunmed.tv.core.model.AppInfo
import com.lunmed.tv.core.model.CardSizePreset
import com.lunmed.tv.core.ui.artwork.AppArtwork
import com.lunmed.tv.core.ui.artwork.AppArtworkCache
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

    val (cardWidth, cardHeight) = remember(cardSize) {
        when (cardSize) {
            CardSizePreset.COMPACT -> 164.dp to 92.dp
            CardSizePreset.STANDARD -> 196.dp to 110.dp
            CardSizePreset.CINEMA -> 232.dp to 130.dp
        }
    }

    val artwork by produceState<AppArtwork?>(
        initialValue = null,
        key1 = app.packageName,
        key2 = app.activityName,
    ) {
        value = AppArtworkCache.load(context, app.packageName, app.activityName)
    }

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
                .onFocusChanged { if (it.isFocused) onFocused() },
        ) { isFocused ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                colors.surfaceElevated,
                                colors.surfaceCard,
                            ),
                        ),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                val loaded = artwork
                if (loaded != null) {
                    if (loaded.isBanner) {
                        Image(
                            bitmap = loaded.bitmap,
                            contentDescription = app.label,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                    } else {
                        Image(
                            bitmap = loaded.bitmap,
                            contentDescription = app.label,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(48.dp),
                        )
                    }
                } else {
                    Text(
                        text = app.label.take(2).uppercase(),
                        style = LunmedTheme.typography.titleLarge,
                        color = colors.textMuted,
                    )
                }

                if (app.isFavorite) {
                    Icon(
                        imageVector = Icons.Rounded.Star,
                        contentDescription = "Favorite",
                        tint = if (isFocused) colors.accentPrimary else colors.accentPrimary.copy(alpha = 0.75f),
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .size(14.dp),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(spacing.xs))

        Text(
            text = app.label,
            style = LunmedTheme.typography.titleCard,
            color = colors.textSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 4.dp),
        )
    }
}

