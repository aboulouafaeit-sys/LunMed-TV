package com.lunmed.tv.core.design.tokens

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.spring
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
    val unfocusedScale: Float,
    val unfocusedAlpha: Float,
)

object LunmedFocusStyle {
    val Default = LunmedFocusTokens(
        borderWidth = 2.5.dp,
        focusedScale = 1.10f,
        pressedScale = 0.98f,
        idleScale = 1.0f,
        unfocusedScale = 0.97f,
        unfocusedAlpha = 0.85f,
    )
}

/**
 * Hardware-conscious, interruptible animation timing tokens.
 * Features Apple TV-grade fluid physics springs and silky decelerations.
 */
@Immutable
data class LunmedAnimationTokens(
    val focusDurationMs: Int,
    val transitionDurationMs: Int,
    val panelDurationMs: Int,
    val easing: Easing,
    val springDamping: Float = 0.72f,
    val springStiffness: Float = 380f,
) {
    fun <T> focusSpec(): FiniteAnimationSpec<T> = tween(
        durationMillis = focusDurationMs,
        easing = easing,
    )

    fun <T> transitionSpec(): FiniteAnimationSpec<T> = tween(
        durationMillis = transitionDurationMs,
        easing = easing,
    )

    fun <T> scaleSpringSpec(): AnimationSpec<T> = if (focusDurationMs == 0) {
        snap()
    } else {
        spring(
            dampingRatio = springDamping,
            stiffness = springStiffness,
        )
    }
}

object LunmedAnimationSpecs {
    // Ultra-fluid Apple TV style cubic curve (instant lift, gentle settling)
    private val FluidDecelerate = CubicBezierEasing(0.16f, 1.0f, 0.3f, 1.0f)

    fun forIntensity(intensity: AnimationIntensity): LunmedAnimationTokens = when (intensity) {
        AnimationIntensity.FULL -> LunmedAnimationTokens(
            focusDurationMs = 180,
            transitionDurationMs = 280,
            panelDurationMs = 300,
            easing = FluidDecelerate,
            springDamping = 0.72f,
            springStiffness = 380f,
        )
        AnimationIntensity.REDUCED -> LunmedAnimationTokens(
            focusDurationMs = 100,
            transitionDurationMs = 150,
            panelDurationMs = 160,
            easing = FluidDecelerate,
            springDamping = 0.92f,
            springStiffness = 480f,
        )
        AnimationIntensity.MINIMAL -> LunmedAnimationTokens(
            focusDurationMs = 0,
            transitionDurationMs = 0,
            panelDurationMs = 0,
            easing = FluidDecelerate,
            springDamping = 1.0f,
            springStiffness = 1000f,
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
