package com.lunmed.tv.core.data.apps

import com.lunmed.tv.core.database.entity.AppMetadataEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppDiscoveryMergerTest {

    @Test
    fun `merge excludes self and prefers leanback entry over standard entry`() {
        val leanback = listOf(
            entry(pkg = "com.netflix.ninja", label = "Netflix", isTv = true),
            entry(pkg = "com.lunmed.tv", label = "LUNMED TV", isTv = true),
        )
        val standard = listOf(
            entry(pkg = "com.netflix.ninja", label = "Netflix Mobile", isTv = false),
            entry(pkg = "org.videolan.vlc", label = "VLC", isTv = false),
        )

        val result = AppDiscoveryMerger.merge(
            leanbackEntries = leanback,
            standardEntries = standard,
            metadataByPackage = emptyMap(),
            selfPackageName = "com.lunmed.tv",
        )

        assertEquals(2, result.size)
        assertEquals("Netflix", result[0].label)
        assertTrue(result[0].isTvApp)
        assertEquals("VLC", result[1].label)
        assertFalse(result[1].isTvApp)
    }

    @Test
    fun `merge applies metadata and sorts by customOrder then label`() {
        val leanback = listOf(
            entry(pkg = "com.zeta", label = "Zeta"),
            entry(pkg = "com.alpha", label = "Alpha"),
            entry(pkg = "com.beta", label = "Beta"),
        )
        val metadata = mapOf(
            "com.zeta" to AppMetadataEntity(
                packageName = "com.zeta",
                isFavorite = true,
                customOrder = 0,
                categoryId = "media",
                launchCount = 5,
            ),
            "com.beta" to AppMetadataEntity(
                packageName = "com.beta",
                isHidden = true,
            ),
        )

        val result = AppDiscoveryMerger.merge(
            leanbackEntries = leanback,
            standardEntries = emptyList(),
            metadataByPackage = metadata,
            selfPackageName = "com.lunmed.tv",
        )

        assertEquals(listOf("com.zeta", "com.alpha", "com.beta"), result.map { it.packageName })
        assertTrue(result[0].isFavorite)
        assertEquals("media", result[0].categoryId)
        assertEquals(5, result[0].launchCount)
        assertTrue(result[2].isHidden)
    }

    @Test
    fun `merge uses packageName when label is blank`() {
        val result = AppDiscoveryMerger.merge(
            leanbackEntries = listOf(entry(pkg = "com.example.blank", label = "   ")),
            standardEntries = emptyList(),
            metadataByPackage = emptyMap(),
            selfPackageName = "com.lunmed.tv",
        )

        assertEquals("com.example.blank", result.single().label)
    }

    private fun entry(
        pkg: String,
        label: String,
        isTv: Boolean = true,
    ) = DiscoveredAppEntry(
        packageName = pkg,
        activityName = "$pkg.MainActivity",
        label = label,
        isTvApp = isTv,
        isSystemApp = false,
        inferredCategoryId = "other",
    )
}

