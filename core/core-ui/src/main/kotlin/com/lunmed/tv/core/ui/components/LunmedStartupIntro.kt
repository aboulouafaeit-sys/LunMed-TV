package com.lunmed.tv.core.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Tv
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
import androidx.compose.ui.unit.sp
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import com.lunmed.tv.core.design.theme.LunmedTheme
import kotlinx.coroutines.delay

/**
 * Ultra-luxury, cinematic cold-boot startup intro animation for LUNMED TV.
 * Displays only once on app startup for 1.4s, dismissible instantly by any remote key press.
 */
@Composable
fun LunmedStartupIntro(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = LunmedTheme.colors

    val alphaAnim = remember { Animatable(0f) }
    val scaleAnim = remember { Animatable(0.88f) }

    LaunchedEffect(Unit) {
        // Fast, smooth cinematic reveal
        alphaAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 450, easing = FastOutSlowInEasing),
        )
        scaleAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 550, easing = FastOutSlowInEasing),
        )
        // Hold for 750ms then dismiss
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
        // Ambient radial background glow
        Box(
            modifier = Modifier
                .size(360.dp)
                .alpha(alphaAnim.value * 0.35f)
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

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .alpha(alphaAnim.value)
                .scale(scaleAnim.value),
        ) {
            // Glowing Emblem
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(colors.surfaceElevated, CircleShape)
                    .border(1.5.dp, colors.accentPrimary, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Rounded.Tv,
                    contentDescription = "LUNMED TV",
                    tint = colors.accentPrimary,
                    modifier = Modifier.size(42.dp),
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Brand Typography
            Text(
                text = "LUNMED TV",
                style = LunmedTheme.typography.displayHero.copy(
                    letterSpacing = 6.sp,
                ),
                color = colors.textPrimary,
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "CINEMATIC ENTERTAINMENT LAUNCHER",
                style = LunmedTheme.typography.labelAction.copy(
                    letterSpacing = 3.sp,
                ),
                color = colors.accentPrimary.copy(alpha = 0.85f),
            )
        }
    }
}
