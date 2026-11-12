package com.lunmed.tv.core.data.apps

import com.lunmed.tv.core.database.entity.AppMetadataEntity
import com.lunmed.tv.core.model.AppInfo

/**
 * Raw discovered entry from Android PackageManager before merging with user metadata.
 */
data class DiscoveredAppEntry(
    val packageName: String,
    val activityName: String,
    val label: String,
    val isTvApp: Boolean,
    val isSystemApp: Boolean,
    val inferredCategoryId: String,
)

/**
 * Pure domain mapper that deduplicates Leanback + standard launcher entries,
 * excludes self, merges Room metadata, and applies deterministic ordering.
 */
object AppDiscoveryMerger {

    fun merge(
        leanbackEntries: List<DiscoveredAppEntry>,
        standardEntries: List<DiscoveredAppEntry>,
        metadataByPackage: Map<String, AppMetadataEntity>,
        selfPackageName: String,
    ): List<AppInfo> {
        val deduplicated = LinkedHashMap<String, DiscoveredAppEntry>()

        // Prefer Leanback TV entries over standard phone/tablet launcher entries.
        for (entry in leanbackEntries) {
            if (entry.packageName != selfPackageName) {
                deduplicated.putIfAbsent(entry.packageName, entry)
            }
        }
        for (entry in standardEntries) {
            if (entry.packageName != selfPackageName) {
                deduplicated.putIfAbsent(entry.packageName, entry)
            }
        }

        return deduplicated.values
            .map { entry ->
                val meta = metadataByPackage[entry.packageName]
                AppInfo(
                    packageName = entry.packageName,
                    activityName = entry.activityName,
                    label = entry.label.ifBlank { entry.packageName },
                    categoryId = meta?.categoryId ?: entry.inferredCategoryId,
                    isFavorite = meta?.isFavorite ?: false,
                    isHidden = meta?.isHidden ?: false,
                    isTvApp = entry.isTvApp,
                    isSystemApp = entry.isSystemApp,
                    customOrder = meta?.customOrder ?: Int.MAX_VALUE,
                    lastLaunchedEpochMs = meta?.lastLaunchedEpochMs ?: 0L,
                    launchCount = meta?.launchCount ?: 0,
                )
            }
            .sortedWith(
                compareBy<AppInfo> { it.customOrder }
                    .thenBy(String.CASE_INSENSITIVE_ORDER) { it.label },
            )
    }
}

