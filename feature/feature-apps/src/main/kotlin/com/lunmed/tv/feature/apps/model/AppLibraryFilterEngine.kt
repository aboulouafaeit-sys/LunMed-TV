package com.lunmed.tv.feature.apps.model

import com.lunmed.tv.core.model.AppInfo

object AppLibraryFilterEngine {

    const val CATEGORY_ALL = "all"
    const val CATEGORY_FAVORITES = "favorites"
    const val CATEGORY_HIDDEN = "hidden"

    val DEFAULT_CATEGORIES = listOf(
        CATEGORY_ALL to "All Apps",
        CATEGORY_FAVORITES to "Favorites",
        "media" to "Entertainment & Media",
        "games" to "Games",
        "utilities" to "Utilities",
        "system" to "System",
        "other" to "Other",
        CATEGORY_HIDDEN to "Hidden Apps",
    )

    fun filterAndSort(
        allApps: List<AppInfo>,
        searchQuery: String,
        selectedCategory: String,
        sortOrder: AppSortOrder,
    ): List<AppInfo> {
        val trimmedQuery = searchQuery.trim()

        return allApps
            .asSequence()
            .filter { app ->
                when (selectedCategory) {
                    CATEGORY_ALL -> !app.isHidden
                    CATEGORY_FAVORITES -> app.isFavorite && !app.isHidden
                    CATEGORY_HIDDEN -> app.isHidden
                    else -> !app.isHidden && (app.categoryId ?: "other") == selectedCategory
                }
            }
            .filter { app ->
                if (trimmedQuery.isBlank()) {
                    true
                } else {
                    app.label.contains(trimmedQuery, ignoreCase = true) ||
                        app.packageName.contains(trimmedQuery, ignoreCase = true)
                }
            }
            .sortedWith(comparatorFor(sortOrder))
            .toList()
    }

    fun computeCategoryCounts(allApps: List<AppInfo>): Map<String, Int> {
        val counts = mutableMapOf<String, Int>()
        var visibleCount = 0
        var favoritesCount = 0
        var hiddenCount = 0

        for (app in allApps) {
            if (app.isHidden) {
                hiddenCount++
            } else {
                visibleCount++
                if (app.isFavorite) favoritesCount++
                val cat = app.categoryId ?: "other"
                counts[cat] = (counts[cat] ?: 0) + 1
            }
        }

        counts[CATEGORY_ALL] = visibleCount
        counts[CATEGORY_FAVORITES] = favoritesCount
        counts[CATEGORY_HIDDEN] = hiddenCount
        return counts
    }

    private fun comparatorFor(sortOrder: AppSortOrder): Comparator<AppInfo> = when (sortOrder) {
        AppSortOrder.NAME_ASC -> compareBy(String.CASE_INSENSITIVE_ORDER) { it.label }
        AppSortOrder.NAME_DESC -> compareByDescending(String.CASE_INSENSITIVE_ORDER) { it.label }
        AppSortOrder.MOST_USED -> compareByDescending<AppInfo> { it.launchCount }
            .thenBy(String.CASE_INSENSITIVE_ORDER) { it.label }
        AppSortOrder.RECENTLY_USED -> compareByDescending<AppInfo> { it.lastLaunchedEpochMs }
            .thenBy(String.CASE_INSENSITIVE_ORDER) { it.label }
        AppSortOrder.CUSTOM -> compareBy<AppInfo> { it.customOrder }
            .thenBy(String.CASE_INSENSITIVE_ORDER) { it.label }
    }
}

