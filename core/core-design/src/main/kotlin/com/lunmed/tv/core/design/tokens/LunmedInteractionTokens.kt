package com.lunmed.tv.core.design.tokens

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lunmed.tv.core.model.AnimationIntensity

/**
 * Focus presentation tokens for 10-foot D-pad navigation.
 */
@Immutable
data class LunmedFocusTokens(
    val borderWidth: Dp,
    val focusedScale: Float,
    val pressedScale: Float,
    val idleScale: Float,
    val unfocusedAlpha: Float,
)

object LunmedFocusStyle {
    val Default = LunmedFocusTokens(
        borderWidth = 2.dp,
        focusedScale = 1.05f,
        pressedScale = 0.98f,
        idleScale = 1.0f,
        unfocusedAlpha = 0.92f,
    )
}

/**
 * Hardware-conscious, interruptible animation timing tokens.
 */
@Immutable
data class LunmedAnimationTokens(
    val focusDurationMs: Int,
    val transitionDurationMs: Int,
    val panelDurationMs: Int,
    val easing: Easing,
) {
    fun <T> focusSpec(): FiniteAnimationSpec<T> = tween(
        durationMillis = focusDurationMs,
        easing = easing,
    )

    fun <T> transitionSpec(): FiniteAnimationSpec<T> = tween(
        durationMillis = transitionDurationMs,
        easing = easing,
    )
}

object LunmedAnimationSpecs {
    private val SmoothDecelerate = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)

    fun forIntensity(intensity: AnimationIntensity): LunmedAnimationTokens = when (intensity) {
        AnimationIntensity.FULL -> LunmedAnimationTokens(
            focusDurationMs = 150,
            transitionDurationMs = 220,
            panelDurationMs = 240,
            easing = SmoothDecelerate,
        )
        AnimationIntensity.REDUCED -> LunmedAnimationTokens(
            focusDurationMs = 90,
            transitionDurationMs = 120,
            panelDurationMs = 140,
            easing = SmoothDecelerate,
        )
        AnimationIntensity.MINIMAL -> LunmedAnimationTokens(
            focusDurationMs = 0,
            transitionDurationMs = 0,
            panelDurationMs = 0,
            easing = SmoothDecelerate,
        )
    }
}

/**
 * Explicit interactive component states for TV remote controls.
 */
enum class LunmedComponentState {
    IDLE,
    FOCUSED,
    PRESSED,
    SELECTED,
    DISABLED,
}

