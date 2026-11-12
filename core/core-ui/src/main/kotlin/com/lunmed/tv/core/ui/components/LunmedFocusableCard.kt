package com.lunmed.tv.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Surface
import com.lunmed.tv.core.design.theme.LunmedTheme

/**
 * TV-first focusable card implementing LUNMED Signature focus presentation.
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
    val borderColor by animateColorAsState(
        targetValue = if (isFocused) colors.borderFocused else colors.borderSubtle,
        animationSpec = animTokens.focusSpec(),
        label = "LunmedCardBorder",
    )

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
                border = BorderStroke(focusTokens.borderWidth / 2, borderColor),
                shape = shape,
            ),
            focusedBorder = Border(
                border = BorderStroke(focusTokens.borderWidth, borderColor),
                shape = shape,
            ),
        ),
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .onFocusChanged { isFocused = it.isFocused },
    ) {
        Box {
            content(isFocused)
        }
    }
}

