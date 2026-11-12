package com.lunmed.tv.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DeviceDiagnosticsTest {

    @Test
    fun memoryDiagnostics_formatsCorrectly() {
        val total = 4L * 1024 * 1024 * 1024 // 4 GB
        val available = 1L * 1024 * 1024 * 1024 // 1 GB
        val used = total - available // 3 GB
        val ratio = 3f / 4f

        val mem = MemoryDiagnostics(
            totalBytes = total,
            availableBytes = available,
            usedBytes = used,
            usageRatio = ratio,
            isLowMemory = false,
            thresholdBytes = 500L * 1024 * 1024,
        )

        assertEquals("4.0 GB", mem.totalGbFormatted)
        assertEquals("3.0 GB", mem.usedGbFormatted)
        assertEquals("1.0 GB", mem.availableGbFormatted)
        assertEquals("75%", mem.usagePercentageText)
        assertFalse(mem.isLowMemory)
    }

    @Test
    fun storageDiagnostics_formatsCorrectly() {
        val total = 32L * 1024 * 1024 * 1024 // 32 GB
        val available = 16L * 1024 * 1024 * 1024 // 16 GB
        val used = 16L * 1024 * 1024 * 1024
        val ratio = 0.5f

        val storage = StorageDiagnostics(
            totalBytes = total,
            availableBytes = available,
            usedBytes = used,
            usageRatio = ratio,
        )

        assertEquals("32.0 GB", storage.totalGbFormatted)
        assertEquals("16.0 GB", storage.usedGbFormatted)
        assertEquals("16.0 GB", storage.availableGbFormatted)
        assertEquals("50%", storage.usagePercentageText)
    }

    @Test
    fun displayDiagnostics_detects4k() {
        val display = DisplayDiagnostics(
            resolutionWidth = 3840,
            resolutionHeight = 2160,
            refreshRateHz = 60.0f,
            densityDpi = 320,
            is4k = true,
            hdrSupportSummary = "HDR10, HLG",
        )

        assertTrue(display.is4k)
        assertEquals(3840, display.resolutionWidth)
        assertEquals("HDR10, HLG", display.hdrSupportSummary)
    }
}

