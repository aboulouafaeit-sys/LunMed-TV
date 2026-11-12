package com.lunmed.tv.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
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
 * World-class TV focusable card implementing Apple TV (tvOS) & Google TV Horizon grade
 * interaction physics:
 * 1. Physical spring overshoot & settling on D-pad navigation.
 * 2. 45° specular light reflection sheen sweep upon receiving focus.
 * 3. Deep ambient atmospheric drop shadow and 3D Z-index lift.
 * 4. Specular champagne-gold beveled rim illumination.
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

    // Silky, physics-based spring scaling (Apple TV signature feel)
    val scale by animateFloatAsState(
        targetValue = if (isFocused) focusTokens.focusedScale else focusTokens.idleScale,
        animationSpec = animTokens.scaleSpringSpec(),
        label = "LunmedCardSpringScale",
    )

    // Specular light reflection sheen sweep across glass face
    val sheenProgress = remember { Animatable(0f) }
    LaunchedEffect(isFocused) {
        if (isFocused) {
            sheenProgress.snapTo(0f)
            sheenProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 440, easing = LinearOutSlowInEasing),
            )
        } else {
            sheenProgress.snapTo(0f)
        }
    }

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
            .zIndex(if (isFocused) 50f else 1f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                shadowElevation = if (isFocused) 22f else 2f
            }
            .onFocusChanged { isFocused = it.isFocused },
    ) {
        Box(modifier = Modifier.clip(shape)) {
            // 1. Primary Card Content
            content(isFocused)

            // 2. Ambient Inner Highlight Hairline
            if (isFocused) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .drawWithContent {
                            drawContent()
                            drawRect(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0x38FFFFFF),
                                        Color.Transparent,
                                    ),
                                    startY = 0f,
                                    endY = size.height * 0.4f,
                                ),
                            )
                        },
                )
            }

            // 3. Apple TV 45° Diagonal Specular Light Reflection Sweep
            if (isFocused && sheenProgress.value > 0f && sheenProgress.value < 1f) {
                val progress = sheenProgress.value
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .drawWithContent {
                            drawContent()
                            val w = size.width
                            val h = size.height
                            val offset = (progress * (w + h * 1.5f)) - (h * 0.5f)
                            drawRect(
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.White.copy(alpha = 0.18f),
                                        Color.White.copy(alpha = 0.32f),
                                        Color.White.copy(alpha = 0.18f),
                                        Color.Transparent,
                                    ),
                                    start = Offset(offset - 80f, 0f),
                                    end = Offset(offset + 80f, h),
                                ),
                            )
                        },
                )
            }
        }
    }
}
