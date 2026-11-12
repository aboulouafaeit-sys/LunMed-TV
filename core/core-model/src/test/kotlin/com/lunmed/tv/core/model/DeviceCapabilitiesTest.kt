package com.lunmed.tv.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DeviceCapabilitiesTest {

    @Test
    fun `is4kDisplay returns true for 3840x2160`() {
        val caps = sampleCapabilities(width = 3840, height = 2160)
        assertTrue(caps.is4kDisplay)
    }

    @Test
    fun `is4kDisplay returns false for 1920x1080`() {
        val caps = sampleCapabilities(width = 1920, height = 1080)
        assertFalse(caps.is4kDisplay)
    }

    @Test
    fun `low ram device recommends reduced animation intensity`() {
        val lowRam = sampleCapabilities(isLowRam = true)
        val normalRam = sampleCapabilities(isLowRam = false)

        assertEquals(AnimationIntensity.REDUCED, lowRam.recommendedAnimationIntensity)
        assertEquals(AnimationIntensity.FULL, normalRam.recommendedAnimationIntensity)
    }

    @Test
    fun `launcher preferences defaults are valid`() {
        val defaults = LauncherPreferences()
        assertEquals(AccentPreset.CHAMPAGNE_GOLD, defaults.accentPreset)
        assertEquals(InterfaceDensity.COMFORTABLE, defaults.density)
        assertEquals(AnimationIntensity.FULL, defaults.animationIntensity)
        assertTrue(defaults.showClock)
    }

    private fun sampleCapabilities(
        width: Int = 1920,
        height: Int = 1080,
        isLowRam: Boolean = false,
    ) = DeviceCapabilities(
        sdkInt = 34,
        isLeanbackSupported = true,
        isTelevisionUiMode = true,
        hasTouchscreen = false,
        hasDpad = true,
        isLowRamDevice = isLowRam,
        screenWidthPx = width,
        screenHeightPx = height,
        densityDpi = 320,
    )
}

