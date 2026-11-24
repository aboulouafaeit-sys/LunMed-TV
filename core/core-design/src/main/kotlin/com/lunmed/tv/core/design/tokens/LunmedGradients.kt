package com.lunmed.tv.core.design.tokens

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

/**
 * Accent-coherent gradient recipes shared by every focused surface in the launcher.
 *
 * Before this existed each component hand-rolled the champagne-gold rim
 * (0xFFFFF8E7 -> accentPrimary -> 0xFFC49A45), so switching the accent preset to
 * ICE_CYAN, PLATINUM_SILVER or OBSIDIAN_AMBER still produced a gold focus ring.
 * The rim is now derived from the active accent tokens only, which keeps the
 * focus language consistent across Home, Apps, Device Center and Settings.
 */
object LunmedGradients {

    /** How far the top rim stop is mixed towards pure white. */
    private const val RIM_HIGHLIGHT_TOWARD_WHITE = 0.60f

    /** How far the bottom rim stop is mixed towards pure black. */
    private const val RIM_SHADOW_TOWARD_BLACK = 0.20f

    /**
     * Four-stop specular rim: lit bevel at the top, the two accent tones in the
     * middle and a darker bevel at the bottom - the "machined edge" look.
     */
    fun focusRimStops(colors: LunmedColorTokens): List<Color> = listOf(
        lerp(colors.accentPrimary, Color.White, RIM_HIGHLIGHT_TOWARD_WHITE),
        colors.accentPrimary,
        colors.accentSecondary,
        lerp(colors.accentSecondary, Color.Black, RIM_SHADOW_TOWARD_BLACK),
    )

    /** Replacement for the old hardcoded gold focus border. */
    fun focusRimBrush(colors: LunmedColorTokens): Brush =
        Brush.linearGradient(focusRimStops(colors))
}
