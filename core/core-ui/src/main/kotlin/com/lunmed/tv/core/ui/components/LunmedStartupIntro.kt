package com.lunmed.tv.core.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.unit.dp
import com.lunmed.tv.core.design.theme.LunmedTheme
import kotlinx.coroutines.delay

/**
 * Ultra-luxury, cinematic cold-boot startup intro animation for LUNMED TV.
 * Features the signature champagne-gold & obsidian monogram badge.
 * Displays only once on app cold boot for 1.4s, dismissible instantly by any remote key press.
 */
@Composable
fun LunmedStartupIntro(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LunmedTheme.colors

    val alphaAnim = remember { Animatable(0f) }
    val scaleAnim = remember { Animatable(0.90f) }
    val glowScaleAnim = remember { Animatable(0.6f) }

    LaunchedEffect(Unit) {
        // Smooth cinematic reveal
        glowScaleAnim.animateTo(
            targetValue = 1.3f,
            animationSpec = tween(durationMillis = 750, easing = FastOutSlowInEasing),
        )
    }

    LaunchedEffect(Unit) {
        alphaAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
        )
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 550, easing = FastOutSlowInEasing),
        )
        // Hold for 750ms then crossfade out
        delay(750L)
        alphaAnim.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 200, easing = FastOutSlowInEasing),
        )
        onComplete()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(colors.obsidianCanvas)
            .onPreviewKeyEvent { event ->
                if (event.type == KeyEventType.KeyDown) {
                    onComplete()
                    true
                } else {
                    false
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        // Deep Ambient Radial Aura
        Box(
            modifier = Modifier
                .size(420.dp)
                .scale(glowScaleAnim.value)
                .alpha(alphaAnim.value * 0.40f)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            colors.accentGlow,
                            colors.accentPrimary.copy(alpha = 0.08f),
                            Color.Transparent,
                        ),
                    ),
                    shape = CircleShape,
                ),
        )

        // Hero Brand Badge
        Box(
            modifier = Modifier
                .alpha(alphaAnim.value)
                .scale(scaleAnim.value),
            contentAlignment = Alignment.Center,
        ) {
            LunmedBrandBadge(
                size = LunmedBrandBadgeSize.HERO,
                showWordmark = true,
                animatedSheen = true,
            )
        }
    }
}
