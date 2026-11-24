package com.lunmed.tv.core.design

import androidx.compose.ui.graphics.Color
import com.lunmed.tv.core.design.tokens.LunmedAnimationSpecs
import com.lunmed.tv.core.design.tokens.LunmedColors
import com.lunmed.tv.core.design.tokens.LunmedFocusStyle
import com.lunmed.tv.core.design.tokens.LunmedGradients
import com.lunmed.tv.core.design.tokens.LunmedSpacing
import com.lunmed.tv.core.design.tokens.LunmedTypography
import com.lunmed.tv.core.model.AccentPreset
import com.lunmed.tv.core.model.AnimationIntensity
import com.lunmed.tv.core.model.InterfaceDensity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.pow

class LunmedDesignSystemTest {

    @Test
    fun `all accent presets produce distinct non-transparent primary colors`() {
        val primaries = AccentPreset.entries.map { preset ->
            val tokens = LunmedColors.forAccent(preset)
            assertTrue(tokens.accentPrimary.alpha == 1f)
            assertEquals(tokens.accentPrimary, tokens.borderFocused)
            tokens.accentPrimary
        }.toSet()

        assertEquals(AccentPreset.entries.size, primaries.size)
    }

    @Test
    fun `comfortable density has larger spacing than compact density`() {
        val comfortable = LunmedSpacing.forDensity(InterfaceDensity.COMFORTABLE)
        val compact = LunmedSpacing.forDensity(InterfaceDensity.COMPACT)

        assertTrue(comfortable.overscanHorizontal > compact.overscanHorizontal)
        assertTrue(comfortable.sectionGap > compact.sectionGap)
        assertTrue(comfortable.cardGap > compact.cardGap)
    }

    @Test
    fun `animation durations scale down with intensity and zero out on minimal`() {
        val full = LunmedAnimationSpecs.forIntensity(AnimationIntensity.FULL)
        val reduced = LunmedAnimationSpecs.forIntensity(AnimationIntensity.REDUCED)
        val minimal = LunmedAnimationSpecs.forIntensity(AnimationIntensity.MINIMAL)

        assertTrue(full.focusDurationMs > reduced.focusDurationMs)
        assertTrue(reduced.focusDurationMs > 0)
        assertEquals(0, minimal.focusDurationMs)
        assertEquals(0, minimal.transitionDurationMs)
        assertEquals(0, minimal.panelDurationMs)
    }

    @Test
    fun `focus scale is greater than idle scale`() {
        val focus = LunmedFocusStyle.Default
        assertTrue(focus.focusedScale > focus.idleScale)
        assertNotEquals(focus.focusedScale, focus.pressedScale)
    }

    @Test
    fun `focus rim is derived from the active accent preset`() {
        val rims = AccentPreset.entries.map { preset ->
            LunmedGradients.focusRimStops(LunmedColors.forAccent(preset))
        }

        rims.forEach { stops -> assertEquals(4, stops.size) }
        // Regression guard: every preset used to render the same champagne-gold rim.
        assertEquals(AccentPreset.entries.size, rims.toSet().size)

        val goldRim = LunmedGradients.focusRimStops(
            LunmedColors.forAccent(AccentPreset.CHAMPAGNE_GOLD),
        )
        AccentPreset.entries
            .filter { it != AccentPreset.CHAMPAGNE_GOLD }
            .forEach { preset ->
                val stops = LunmedGradients.focusRimStops(LunmedColors.forAccent(preset))
                assertTrue(
                    "$preset still leaks a champagne-gold rim stop",
                    stops.none { it in goldRim },
                )
            }
    }

    @Test
    fun `typography respects the 10-foot legible floor`() {
        val type = LunmedTypography.Default
        val tiers = mapOf(
            "displayHero" to type.displayHero,
            "headlineSection" to type.headlineSection,
            "titleLarge" to type.titleLarge,
            "titleCard" to type.titleCard,
            "bodyPrimary" to type.bodyPrimary,
            "bodySecondary" to type.bodySecondary,
            "labelAction" to type.labelAction,
            "labelMeta" to type.labelMeta,
        )

        tiers.forEach { (name, style) ->
            assertTrue(
                "$name is ${style.fontSize.value}sp, below ${LunmedTypography.MIN_LEGIBLE_SP}sp",
                style.fontSize.value >= LunmedTypography.MIN_LEGIBLE_SP,
            )
        }
    }

    @Test
    fun `text tokens meet WCAG AA contrast on every dark surface`() {
        AccentPreset.entries.forEach { preset ->
            val tokens = LunmedColors.forAccent(preset)
            val textColors = mapOf(
                "textPrimary" to tokens.textPrimary,
                "textSecondary" to tokens.textSecondary,
                "textMuted" to tokens.textMuted,
            )
            val backgrounds = mapOf(
                "obsidianCanvas" to tokens.obsidianCanvas,
                "surfaceCard" to tokens.surfaceCard,
            )

            textColors.forEach { (textName, text) ->
                backgrounds.forEach { (bgName, background) ->
                    val ratio = contrastRatio(text, background)
                    assertTrue(
                        "$textName on $bgName is ${"%.2f".format(ratio)}:1",
                        ratio >= 4.5,
                    )
                }
            }
        }
    }

    private fun contrastRatio(foreground: Color, background: Color): Double {
        val a = relativeLuminance(foreground)
        val b = relativeLuminance(background)
        return (maxOf(a, b) + 0.05) / (minOf(a, b) + 0.05)
    }

    private fun relativeLuminance(color: Color): Double {
        fun channel(value: Float): Double {
            val c = value.toDouble()
            return if (c <= 0.03928) c / 12.92 else ((c + 0.055) / 1.055).pow(2.4)
        }
        return 0.2126 * channel(color.red) +
            0.7152 * channel(color.green) +
            0.0722 * channel(color.blue)
    }
}

