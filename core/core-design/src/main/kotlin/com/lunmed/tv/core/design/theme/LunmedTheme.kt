package com.lunmed.tv.core.design.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.darkColorScheme
import com.lunmed.tv.core.design.tokens.LunmedAnimationSpecs
import com.lunmed.tv.core.design.tokens.LunmedAnimationTokens
import com.lunmed.tv.core.design.tokens.LunmedColorTokens
import com.lunmed.tv.core.design.tokens.LunmedColors
import com.lunmed.tv.core.design.tokens.LunmedElevation
import com.lunmed.tv.core.design.tokens.LunmedElevationTokens
import com.lunmed.tv.core.design.tokens.LunmedFocusStyle
import com.lunmed.tv.core.design.tokens.LunmedFocusTokens
import com.lunmed.tv.core.design.tokens.LunmedShapeTokens
import com.lunmed.tv.core.design.tokens.LunmedShapes
import com.lunmed.tv.core.design.tokens.LunmedSpacing
import com.lunmed.tv.core.design.tokens.LunmedSpacingTokens
import com.lunmed.tv.core.design.tokens.LunmedTypography
import com.lunmed.tv.core.design.tokens.LunmedTypographyTokens
import com.lunmed.tv.core.model.AccentPreset
import com.lunmed.tv.core.model.AnimationIntensity
import com.lunmed.tv.core.model.InterfaceDensity

val LocalLunmedColors = staticCompositionLocalOf { LunmedColors.forAccent(AccentPreset.CHAMPAGNE_GOLD) }
val LocalLunmedTypography = staticCompositionLocalOf { LunmedTypography.Default }
val LocalLunmedSpacing = staticCompositionLocalOf { LunmedSpacing.forDensity(InterfaceDensity.COMFORTABLE) }
val LocalLunmedShapes = staticCompositionLocalOf { LunmedShapes.Default }
val LocalLunmedElevation = staticCompositionLocalOf { LunmedElevation.Default }
val LocalLunmedFocus = staticCompositionLocalOf { LunmedFocusStyle.Default }
val LocalLunmedAnimation = staticCompositionLocalOf { LunmedAnimationSpecs.forIntensity(AnimationIntensity.FULL) }

object LunmedTheme {
    val colors: LunmedColorTokens
        @Composable @ReadOnlyComposable get() = LocalLunmedColors.current

    val typography: LunmedTypographyTokens
        @Composable @ReadOnlyComposable get() = LocalLunmedTypography.current

    val spacing: LunmedSpacingTokens
        @Composable @ReadOnlyComposable get() = LocalLunmedSpacing.current

    val shapes: LunmedShapeTokens
        @Composable @ReadOnlyComposable get() = LocalLunmedShapes.current

    val elevation: LunmedElevationTokens
        @Composable @ReadOnlyComposable get() = LocalLunmedElevation.current

    val focus: LunmedFocusTokens
        @Composable @ReadOnlyComposable get() = LocalLunmedFocus.current

    val animation: LunmedAnimationTokens
        @Composable @ReadOnlyComposable get() = LocalLunmedAnimation.current
}

@Composable
fun LunmedTheme(
    accentPreset: AccentPreset = AccentPreset.CHAMPAGNE_GOLD,
    density: InterfaceDensity = InterfaceDensity.COMFORTABLE,
    animationIntensity: AnimationIntensity = AnimationIntensity.FULL,
    content: @Composable () -> Unit,
) {
    val colorTokens = LunmedColors.forAccent(accentPreset)
    val spacingTokens = LunmedSpacing.forDensity(density)
    val animationTokens = LunmedAnimationSpecs.forIntensity(animationIntensity)

    val tvColorScheme = darkColorScheme(
        primary = colorTokens.accentPrimary,
        onPrimary = colorTokens.textOnAccent,
        secondary = colorTokens.accentSecondary,
        onSecondary = colorTokens.textOnAccent,
        background = colorTokens.obsidianCanvas,
        onBackground = colorTokens.textPrimary,
        surface = colorTokens.surfaceDeep,
        onSurface = colorTokens.textPrimary,
        surfaceVariant = colorTokens.surfaceCard,
        onSurfaceVariant = colorTokens.textSecondary,
        error = colorTokens.statusError,
    )

    CompositionLocalProvider(
        LocalLunmedColors provides colorTokens,
        LocalLunmedTypography provides LunmedTypography.Default,
        LocalLunmedSpacing provides spacingTokens,
        LocalLunmedShapes provides LunmedShapes.Default,
        LocalLunmedElevation provides LunmedElevation.Default,
        LocalLunmedFocus provides LunmedFocusStyle.Default,
        LocalLunmedAnimation provides animationTokens,
    ) {
        MaterialTheme(
            colorScheme = tvColorScheme,
            content = content,
        )
    }
}

