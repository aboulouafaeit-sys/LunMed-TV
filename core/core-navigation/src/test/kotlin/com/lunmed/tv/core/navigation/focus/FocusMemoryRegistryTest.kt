package com.lunmed.tv.core.navigation.focus

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FocusMemoryRegistryTest {

    @Test
    fun `records and restores per-section focus independently`() {
        val registry = FocusMemoryRegistry()

        registry.recordFocus("favorites", "com.netflix.ninja")
        registry.recordFocus("games", "com.asphalt")

        assertEquals("com.netflix.ninja", registry.getLastFocusedItem("favorites"))
        assertEquals("com.asphalt", registry.getLastFocusedItem("games"))
        assertEquals("games", registry.activeSection)
    }

    @Test
    fun `push and pop overlay anchor restores previous focus target`() {
        val registry = FocusMemoryRegistry()

        registry.recordFocus("favorites", "com.youtube.tv")
        registry.pushOverlayAnchor()

        registry.recordFocus("media", "com.plexapp")
        registry.pushOverlayAnchor()

        val second = registry.popOverlayAnchor()
        val first = registry.popOverlayAnchor()
        val empty = registry.popOverlayAnchor()

        assertEquals(FocusMemoryRegistry.FocusAnchor("media", "com.plexapp"), second)
        assertEquals(FocusMemoryRegistry.FocusAnchor("favorites", "com.youtube.tv"), first)
        assertNull(empty)
    }

    @Test
    fun `pruneMissingItems removes stale package from section memory`() {
        val registry = FocusMemoryRegistry()
        registry.recordFocus("favorites", "com.removed.app")

        registry.pruneMissingItems("favorites", setOf("com.kept.app"))
        assertNull(registry.getLastFocusedItem("favorites"))

        registry.clear()
        assertNull(registry.activeSection)
    }
}

