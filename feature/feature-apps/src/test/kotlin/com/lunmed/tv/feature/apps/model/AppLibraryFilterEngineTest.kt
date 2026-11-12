package com.lunmed.tv.feature.apps.model

import com.lunmed.tv.core.model.AppInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppLibraryFilterEngineTest {

    private val sampleApps = listOf(
        app("com.netflix", "Netflix", categoryId = "media", isFavorite = true, lastLaunched = 500L, launchCount = 10, customOrder = 2),
        app("com.youtube", "YouTube", categoryId = "media", isFavorite = false, lastLaunched = 800L, launchCount = 25, customOrder = 1),
        app("com.asphalt", "Asphalt 9", categoryId = "games", isFavorite = true, lastLaunched = 0L, launchCount = 3, customOrder = 3),
        app("com.vlc", "VLC Media Player", categoryId = "media", isFavorite = false, lastLaunched = 100L, launchCount = 1, customOrder = 4),
        app("com.secret.tool", "Secret Diagnostic", categoryId = "system", isHidden = true, customOrder = 5),
    )

    @Test
    fun `filterAndSort by ALL excludes hidden apps and sorts alphabetically asc by default`() {
        val result = AppLibraryFilterEngine.filterAndSort(
            allApps = sampleApps,
            searchQuery = "",
            selectedCategory = AppLibraryFilterEngine.CATEGORY_ALL,
            sortOrder = AppSortOrder.NAME_ASC,
        )

        assertEquals(4, result.size)
        assertEquals(listOf("Asphalt 9", "Netflix", "VLC Media Player", "YouTube"), result.map { it.label })
        assertFalse(result.any { it.isHidden })
    }

    @Test
    fun `filterAndSort by FAVORITES returns only non-hidden favorites`() {
        val result = AppLibraryFilterEngine.filterAndSort(
            allApps = sampleApps,
            searchQuery = "",
            selectedCategory = AppLibraryFilterEngine.CATEGORY_FAVORITES,
            sortOrder = AppSortOrder.NAME_ASC,
        )

        assertEquals(2, result.size)
        assertTrue(result.all { it.isFavorite })
        assertEquals(listOf("Asphalt 9", "Netflix"), result.map { it.label })
    }

    @Test
    fun `filterAndSort by HIDDEN returns only hidden apps`() {
        val result = AppLibraryFilterEngine.filterAndSort(
            allApps = sampleApps,
            searchQuery = "",
            selectedCategory = AppLibraryFilterEngine.CATEGORY_HIDDEN,
            sortOrder = AppSortOrder.NAME_ASC,
        )

        assertEquals(1, result.size)
        assertEquals("com.secret.tool", result.single().packageName)
        assertTrue(result.single().isHidden)
    }

    @Test
    fun `filterAndSort by category filters correctly`() {
        val games = AppLibraryFilterEngine.filterAndSort(
            allApps = sampleApps,
            searchQuery = "",
            selectedCategory = "games",
            sortOrder = AppSortOrder.NAME_ASC,
        )

        assertEquals(1, games.size)
        assertEquals("Asphalt 9", games.single().label)
    }

    @Test
    fun `filterAndSort by search query matches label and packageName case-insensitively`() {
        val matchByLabel = AppLibraryFilterEngine.filterAndSort(
            allApps = sampleApps,
            searchQuery = "you",
            selectedCategory = AppLibraryFilterEngine.CATEGORY_ALL,
            sortOrder = AppSortOrder.NAME_ASC,
        )
        assertEquals(1, matchByLabel.size)
        assertEquals("YouTube", matchByLabel.single().label)

        val matchByPackage = AppLibraryFilterEngine.filterAndSort(
            allApps = sampleApps,
            searchQuery = "netflix",
            selectedCategory = AppLibraryFilterEngine.CATEGORY_ALL,
            sortOrder = AppSortOrder.NAME_ASC,
        )
        assertEquals(1, matchByPackage.size)
        assertEquals("Netflix", matchByPackage.single().label)
    }

    @Test
    fun `filterAndSort verifies sort orders`() {
        val mostUsed = AppLibraryFilterEngine.filterAndSort(
            allApps = sampleApps,
            searchQuery = "",
            selectedCategory = AppLibraryFilterEngine.CATEGORY_ALL,
            sortOrder = AppSortOrder.MOST_USED,
        )
        assertEquals(listOf("YouTube", "Netflix", "Asphalt 9", "VLC Media Player"), mostUsed.map { it.label })

        val recentlyUsed = AppLibraryFilterEngine.filterAndSort(
            allApps = sampleApps,
            searchQuery = "",
            selectedCategory = AppLibraryFilterEngine.CATEGORY_ALL,
            sortOrder = AppSortOrder.RECENTLY_USED,
        )
        assertEquals(listOf("YouTube", "Netflix", "VLC Media Player", "Asphalt 9"), recentlyUsed.map { it.label })

        val custom = AppLibraryFilterEngine.filterAndSort(
            allApps = sampleApps,
            searchQuery = "",
            selectedCategory = AppLibraryFilterEngine.CATEGORY_ALL,
            sortOrder = AppSortOrder.CUSTOM,
        )
        assertEquals(listOf("YouTube", "Netflix", "Asphalt 9", "VLC Media Player"), custom.map { it.label })

        val desc = AppLibraryFilterEngine.filterAndSort(
            allApps = sampleApps,
            searchQuery = "",
            selectedCategory = AppLibraryFilterEngine.CATEGORY_ALL,
            sortOrder = AppSortOrder.NAME_DESC,
        )
        assertEquals(listOf("YouTube", "VLC Media Player", "Netflix", "Asphalt 9"), desc.map { it.label })
    }

    @Test
    fun `computeCategoryCounts calculates all counts properly`() {
        val counts = AppLibraryFilterEngine.computeCategoryCounts(sampleApps)

        assertEquals(4, counts[AppLibraryFilterEngine.CATEGORY_ALL])
        assertEquals(2, counts[AppLibraryFilterEngine.CATEGORY_FAVORITES])
        assertEquals(1, counts[AppLibraryFilterEngine.CATEGORY_HIDDEN])
        assertEquals(3, counts["media"])
        assertEquals(1, counts["games"])
    }

    private fun app(
        pkg: String,
        label: String,
        categoryId: String = "other",
        isFavorite: Boolean = false,
        isHidden: Boolean = false,
        lastLaunched: Long = 0L,
        launchCount: Int = 0,
        customOrder: Int = Int.MAX_VALUE,
    ) = AppInfo(
        packageName = pkg,
        activityName = "$pkg.Main",
        label = label,
        categoryId = categoryId,
        isFavorite = isFavorite,
        isHidden = isHidden,
        lastLaunchedEpochMs = lastLaunched,
        launchCount = launchCount,
        customOrder = customOrder,
    )
}

