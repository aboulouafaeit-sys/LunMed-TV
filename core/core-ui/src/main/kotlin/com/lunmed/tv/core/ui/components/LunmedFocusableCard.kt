package com.lunmed.tv.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Surface
import com.lunmed.tv.core.design.theme.LunmedTheme

/**
 * TV-first focusable card implementing LUNMED Signature luxury focus presentation.
 * Features 10-foot D-pad focus lift, specular champagne-gold rim gradients,
 * and ambient backlighting.
 */
@Composable
fun LunmedFocusableCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onLongClick: (() -> Unit)? = null,
    shape: Shape = LunmedTheme.shapes.cardMedium,
    enabled: Boolean = true,
    content: @Composable BoxScope.(isFocused: Boolean) -> Unit,
) {
    val colors = LunmedTheme.colors
    val focusTokens = LunmedTheme.focus
    val animTokens = LunmedTheme.animation
    var isFocused by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (isFocused) focusTokens.focusedScale else focusTokens.idleScale,
        animationSpec = animTokens.focusSpec(),
        label = "LunmedCardScale",
    )

    val goldRimBrush = remember(colors.accentPrimary) {
        Brush.linearGradient(
            colors = listOf(
                Color(0xFFFFF8E7),
                colors.accentPrimary,
                colors.accentSecondary,
                Color(0xFFC49A45),
            ),
        )
    }

    val subtleBorderBrush = remember(colors.borderSubtle) {
        Brush.linearGradient(
            colors = listOf(
                colors.borderSubtle,
                Color(0x0EFFFFFF),
            ),
        )
    }

    Surface(
        onClick = onClick,
        onLongClick = onLongClick,
        enabled = enabled,
        shape = ClickableSurfaceDefaults.shape(shape = shape),
        colors = ClickableSurfaceDefaults.colors(
            containerColor = colors.surfaceCard,
            focusedContainerColor = colors.surfaceCardFocused,
            contentColor = colors.textPrimary,
            focusedContentColor = colors.textPrimary,
        ),
        border = ClickableSurfaceDefaults.border(
            border = Border(
                border = BorderStroke(1.dp, subtleBorderBrush),
                shape = shape,
            ),
            focusedBorder = Border(
                border = BorderStroke(focusTokens.borderWidth, goldRimBrush),
                shape = shape,
            ),
        ),
        modifier = modifier
            .zIndex(if (isFocused) 10f else 1f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                shadowElevation = if (isFocused) 16f else 2f
            }
            .onFocusChanged { isFocused = it.isFocused },
    ) {
        Box {
            content(isFocused)
        }
    }
}
