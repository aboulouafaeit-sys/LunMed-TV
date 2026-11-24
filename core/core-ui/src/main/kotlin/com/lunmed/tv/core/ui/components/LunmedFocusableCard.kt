package com.lunmed.tv.core.ui.components

import android.os.SystemClock
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.tv.material3.Border
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.Surface
import com.lunmed.tv.core.design.theme.LunmedTheme
import com.lunmed.tv.core.design.tokens.LunmedGradients
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * World-class TV focusable card implementing Apple TV (tvOS) & Google TV Horizon grade
 * interaction physics:
 * 1. Physical spring overshoot & settling on D-pad navigation.
 * 2. 45° specular light reflection sheen sweep upon receiving focus.
 * 3. Deep ambient atmospheric drop shadow and 3D Z-index lift.
 * 4. Specular champagne-gold beveled rim illumination.
 * 5. Robust D-pad long-press debounce and KeyUp consumption guard to prevent accidental launches.
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
    val coroutineScope = rememberCoroutineScope()
    var isFocused by remember { mutableStateOf(false) }

    // D-pad press feedback: the shared pressedScale token was previously dead code,
    // so OK never gave any tactile response before launching an app.
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val density = LocalDensity.current
    val elevationTokens = LunmedTheme.elevation

    // Bulletproof long-press / release debounce guard:
    // Prevents KeyUp on remote D-pad from triggering onClick when releasing after long press
    var isLongPressConsumed by remember { mutableStateOf(false) }
    var longPressJob by remember { mutableStateOf<Job?>(null) }
    var lastLongPressTime by remember { mutableStateOf(0L) }

    val guardedOnClick: () -> Unit = {
        val now = SystemClock.uptimeMillis()
        if (now - lastLongPressTime > 750L && !isLongPressConsumed) {
            onClick()
        }
    }

    val guardedOnLongClick: (() -> Unit)? = onLongClick?.let { action ->
        {
            lastLongPressTime = SystemClock.uptimeMillis()
            isLongPressConsumed = true
            action()
        }
    }

    // Silky, physics-based spring scaling (Apple TV signature feel):
    // focused lifts the card, pressed dips it back so OK always feels acknowledged.
    val scale by animateFloatAsState(
        targetValue = when {
            // Pressed is applied *relative* to the focused scale (1.10 -> ~1.08) so OK
            // reads as a tactile dip instead of a jarring shrink back to card size.
            isPressed && isFocused -> focusTokens.focusedScale * focusTokens.pressedScale
            isPressed -> focusTokens.pressedScale
            isFocused -> focusTokens.focusedScale
            else -> focusTokens.idleScale
        },
        animationSpec = animTokens.scaleSpringSpec(),
        label = "LunmedCardSpringScale",
    )

    // Token-driven depth: focused cards lift on the shared elevation scale instead of
    // the previous raw 22f pixel constant (which ignored screen density entirely).
    val focusedElevationPx = with(density) { elevationTokens.focused.toPx() }
    val idleElevationPx = with(density) { elevationTokens.subtle.toPx() }
    val cardElevation by animateFloatAsState(
        targetValue = if (isFocused) focusedElevationPx else idleElevationPx,
        animationSpec = animTokens.focusSpec(),
        label = "LunmedCardElevation",
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

    // Accent-coherent specular rim (was hardcoded champagne gold for every preset).
    val focusRimBrush = remember(colors.accentPrimary, colors.accentSecondary) {
        LunmedGradients.focusRimBrush(colors)
    }

    val subtleBorderBrush = remember(colors.borderSubtle) {
        Brush.linearGradient(
            colors = listOf(
                colors.borderSubtle,
                colors.borderGlass,
            ),
        )
    }

    Surface(
        onClick = guardedOnClick,
        onLongClick = guardedOnLongClick,
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
                border = BorderStroke(focusTokens.borderWidth, focusRimBrush),
                shape = shape,
            ),
        ),
        interactionSource = interactionSource,
        modifier = modifier
            .zIndex(if (isFocused) 50f else 1f)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                shadowElevation = cardElevation
            }
            .onFocusChanged { isFocused = it.isFocused }
            .onPreviewKeyEvent { keyEvent ->
                val isConfirmKey = keyEvent.key == Key.DirectionCenter ||
                    keyEvent.key == Key.Enter ||
                    keyEvent.key == Key.NumPadEnter

                if (isConfirmKey && onLongClick != null) {
                    if (keyEvent.type == KeyEventType.KeyDown) {
                        if (keyEvent.nativeKeyEvent.repeatCount == 0) {
                            isLongPressConsumed = false
                            longPressJob?.cancel()
                            longPressJob = coroutineScope.launch {
                                delay(450L)
                                isLongPressConsumed = true
                                lastLongPressTime = SystemClock.uptimeMillis()
                                onLongClick()
                            }
                        }
                    } else if (keyEvent.type == KeyEventType.KeyUp) {
                        longPressJob?.cancel()
                        val wasLongPress = isLongPressConsumed || (SystemClock.uptimeMillis() - lastLongPressTime < 750L)
                        if (wasLongPress) {
                            isLongPressConsumed = false
                            // Swallows KeyUp so Surface does NOT execute onClick on release!
                            return@onPreviewKeyEvent true
                        }
                    }
                }
                false
            },
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
