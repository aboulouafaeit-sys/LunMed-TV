package com.lunmed.tv.core.design

import com.lunmed.tv.core.design.tokens.LunmedAnimationSpecs
import com.lunmed.tv.core.design.tokens.LunmedColors
import com.lunmed.tv.core.design.tokens.LunmedFocusStyle
import com.lunmed.tv.core.design.tokens.LunmedSpacing
import com.lunmed.tv.core.model.AccentPreset
import com.lunmed.tv.core.model.AnimationIntensity
import com.lunmed.tv.core.model.InterfaceDensity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

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
}

